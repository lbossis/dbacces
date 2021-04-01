package org.acme.micrometer;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

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

    private Double getLatestTotalCountOfBuilds() {
        EntityManager entityManager = emFactory.createEntityManager();
        String qry = "SELECT count(*) FROM ArchivedBuilds WHERE temporaryBuild = false";
        return ((Number) entityManager.createQuery(qry).getSingleResult()).doubleValue();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/count")
    public double getCurrentTotalBuildsCount() {
        System.out.println("getCurrentTotalBuildsCount = " + totalBuildsCounter.count());
        return totalBuildsCounter.count();
    }
}