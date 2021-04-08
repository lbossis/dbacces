package org.acme.micrometer;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import io.quarkus.scheduler.Scheduled;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private static final int LOG_INFO_FREQUENCY = 5;

    @Inject
    EntityManagerFactory emFactory;

    @Inject
    MeterRegistry registry;

    private Counter totalBuildsCounter;
    private Counter systemErrorsCounter;
    private Query totalBuildsCountQry;
    private Query systemErrorsCountQry;
    private Query systemErrorsFromToCountQry;
    private long loggingCount = 0;

    @PostConstruct
    void init() {
        EntityManager entityManager = emFactory.createEntityManager();
        totalBuildsCountQry = entityManager.createNamedQuery("ArchivedBuilds.total_count");
        systemErrorsCountQry = entityManager.createNamedQuery("ArchivedBuilds.system_errors_count");
        systemErrorsFromToCountQry = entityManager.createNamedQuery("ArchivedBuilds.system_errors_from_to_count");
        totalBuildsCounter = registry.counter(className + ".total.builds.count");
        systemErrorsCounter = registry.counter(className + ".system.errors.count");
    }

    private Double getTotalBuildsCount() {
        return ((Number) totalBuildsCountQry.getSingleResult()).doubleValue();
    }

    private Double getSystemErrorsCount() {
        return ((Number) systemErrorsCountQry.getSingleResult()).doubleValue();
    }

    private Double getSystemErrorsFromToCount(Date from, Date to) {
        return ((Number) systemErrorsFromToCountQry
                .setParameter("from", from)
                .setParameter("to", to)
                .getSingleResult()).doubleValue();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/total_count")
    public double refreshTotalBuildsCounter() {
        double currentTotalCount = totalBuildsCounter.count();
        double actualTotalCount = getTotalBuildsCount();
        double delta = actualTotalCount - currentTotalCount;
        if (delta > 0) {
            totalBuildsCounter.increment(delta);
            /*
             * Skip reporting count increase at startup since it starts from zero
             * and gets incremented to its current database value
             */
            if (currentTotalCount > 0) {
                log.info("Total counter has been incremented -> " + actualTotalCount);
            }
        } else {
            if (loggingCount % LOG_INFO_FREQUENCY == 0) {
                log.info("refreshTotalBuildsCounter() -> " + actualTotalCount);
            }
        }
        return totalBuildsCounter.count();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/syserr_count")
    public double refreshSystemErrorsCounter() {
        double currentSysErrorsCount = systemErrorsCounter.count();
        double actualSysErrorCount = getSystemErrorsCount();
        double delta = actualSysErrorCount - currentSysErrorsCount;
        if (delta > 0) {
            systemErrorsCounter.increment(delta);
            if (currentSysErrorsCount > 0) {
                log.info("System errors counter has been incremented -> " + actualSysErrorCount);
            }
        } else {
            if (loggingCount % LOG_INFO_FREQUENCY == 0) {
                log.info("refreshSystemErrorsCounter() -> " + actualSysErrorCount);
            }
        }
        return systemErrorsCounter.count();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/syserr_from_to_count")
    public double refreshSystemErrorsFromToCounter(@QueryParam String from, @QueryParam String to) {
        if (from == null || from.isEmpty()) {
            log.error("Invalid 'from' parameter: " + from);
            return 0;
        }
        if (to == null || to.isEmpty()) {
            log.error("Invalid 'to' parameter: " + to);
            return 0;
        }
        double currentSysErrorsCount = systemErrorsCounter.count();
        Date javaDateFrom = symbolic2javaDate(from);
        Date javaDateTo = symbolic2javaDate(to);
        double actualSysErrorCount = getSystemErrorsFromToCount(javaDateFrom, javaDateTo);
        double delta = actualSysErrorCount - currentSysErrorsCount;
        if (delta > 0) {
            systemErrorsCounter.increment(delta);
            if (currentSysErrorsCount > 0) {
                log.info("System errors counter has been incremented -> " + actualSysErrorCount);
            }
        } else {
            if (loggingCount % LOG_INFO_FREQUENCY == 0) {
                log.info("refreshSystemErrorsFromToCounter() -> " + actualSysErrorCount);
            }
        }
        return systemErrorsCounter.count();
    }

    @Scheduled(every = "180s", delay = 0)
    public void refreshCounters() {
        refreshSystemErrorsCounter();
        refreshTotalBuildsCounter();
        loggingCount++;
    }

    private Date symbolic2javaDate(String dt) {
        try {
            Date rs = dateFormatUS.parse(dt);
            if (loggingCount % LOG_INFO_FREQUENCY == 0) {
                log.info(dt + " has been converted to Date: " + rs);
            }
            return rs;
        } catch (ParseException e) {
            log.error(dt + " conversion to Date failed: " + e.getMessage());
        }
        return null;
    }
}
