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
import java.util.List;

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

    private double getLatestTotalCountOfBuilds() {
        EntityManager entityManager = emFactory.createEntityManager();
        String qry = "SELECT count(*) FROM ArchivedBuilds WHERE temporaryBuild = false";
        List rs = entityManager.createQuery(qry).getResultList();
        return (double)(rs.get(0));
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