package org.acme.micrometer;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
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

@Path("/")
@ApplicationScoped
public class DbCnxnViaHibernate {
    private static final String className = DbCnxnViaHibernate.class.getName();
    private static final Logger log = LoggerFactory.getLogger(DbCnxnViaHibernate.class);

    @Inject
    EntityManagerFactory emFactory;

    @Inject
    MeterRegistry registry;

    private Counter totalBuildsCounter;

    @PostConstruct
    void init() {
        totalBuildsCounter = registry.counter(className + ".total.builds.count");
        totalBuildsCounter.increment(getLatestTotalCountOfBuilds());
    }

    private double getLatestTotalCountOfBuilds() {
        EntityManager entityManager = emFactory.createEntityManager();
        String qry = "SELECT count(*) FROM ArchivedBuilds WHERE temporaryBuild = false";
        try {
            return entityManager.createQuery(qry).getFirstResult();
        } catch(ClassCastException e) {
            log.error("count qry failed on cast: " + e.getMessage());
        } catch(Exception e) {
            log.error("count qry failed: " + e.getMessage());
        }
        return 0.0;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/count")
    public double getCurrentTotalBuildsCount() {
        totalBuildsCounter.increment();
        System.out.println("getCurrentTotalBuildsCount = " + totalBuildsCounter.count());
        return totalBuildsCounter.count();
    }
}