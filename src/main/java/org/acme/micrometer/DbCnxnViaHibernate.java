package org.acme.micrometer;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import io.quarkus.scheduler.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

@Path("/")
@ApplicationScoped
public class DbCnxnViaHibernate {
    private static final String className = DbCnxnViaHibernate.class.getName();
    private static final Logger log = LoggerFactory.getLogger(DbCnxnViaHibernate.class);
    private static final String shortDateFormat = "yyyy-MM-dd";
    private static final Locale localeUS = new Locale("en", "US");
    private static final DateFormat dateFormatUS = new SimpleDateFormat(shortDateFormat, localeUS);

    @Inject
    EntityManagerFactory emFactory;

    @Inject
    MeterRegistry registry;

    private EntityManager entityManager;
    private Counter totalBuildsCounter;
    private Counter systemErrorsCounter;
    private String fromDate = "1970-01-01";

    @PostConstruct
    void init() {
        entityManager = emFactory.createEntityManager();
        totalBuildsCounter = registry.counter(className + ".total.builds.count");
        systemErrorsCounter = registry.counter(className + ".system.errors.count");
    }

    private Date symbolic2javaDate(String dt) {
        try {
            Date rs = dateFormatUS.parse(dt);
            log.info("Converted to Date: " + rs);
            return rs;
        } catch (ParseException e) {
            log.error("conversion to Date failed: " + e.getMessage());
        }
        return null;
    }

    private Double getTotalBuildsCount() {
        String qry = "SELECT count(*) FROM ArchivedBuilds WHERE temporaryBuild = false";
        return ((Number) entityManager.createQuery(qry).getSingleResult()).doubleValue();
    }

    private Double getSystemErrorsCount(Date from, Date to) {
        String qry = "SELECT count(*) FROM ArchivedBuilds " +
                " WHERE temporaryBuild = false AND status = 'SYSTEM_ERROR' " +
                " AND startTime >= :from AND endTime <= :to " ;
        return ((Number) entityManager.createQuery(qry)
                .setParameter("from", from)
                .setParameter("to", to)
                .getSingleResult()).doubleValue();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/total_count")
    public double showTotalBuildsCount() {
        double cnt = getTotalBuildsCount();
        double currentTotalCount = totalBuildsCounter.count();
        double delta = cnt - currentTotalCount;
        if (delta > 0) {
            totalBuildsCounter.increment(delta);
            log.info("showTotalBuildsCount: count increased -> " + cnt);

        } else {
            log.info("showTotalBuildsCount: count has not changed -> " + cnt);
        }
        return totalBuildsCounter.count();
    }

    @GET
    @Produces("application/json")
    @Path("/syserr_count?from={from}&to={to}")
    public double showSystemErrorsCount(@PathParam String from, @PathParam String to) {
        if (from == null || from.isEmpty()) {
            log.error("Invalid 'from' parameter: " + from);
            return -1;
        }
        if (to == null || to.isEmpty()) {
            log.error("Invalid 'to' parameter: " + to);
            return -1;
        }
        fromDate = from;
        Date javaDateFrom = symbolic2javaDate(from);
        Date javaDateTo = symbolic2javaDate(to);
        double cnt = getSystemErrorsCount(javaDateFrom, javaDateTo);
        log.info("showSystemErrorsCount() -> " + cnt);
        systemErrorsCounter.increment(cnt);
        return systemErrorsCounter.count();
    }

    @Scheduled(every = "180s", delay = 0)
    public void refreshCounters() {
        String now = DateTimeFormatter.ofPattern(shortDateFormat).format(LocalDateTime.now());
        showSystemErrorsCount(fromDate, now);
        showTotalBuildsCount();
    }
}
