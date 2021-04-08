package org.acme.micrometer;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name="_archived_buildrecords")
@Access(value=AccessType.FIELD)
@NamedQueries({
    @NamedQuery(
            name = "ArchivedBuilds.total_count",
            query = "SELECT count(*) FROM ArchivedBuilds a WHERE a.temporaryBuild = false"),
    @NamedQuery(
            name = "ArchivedBuilds.system_errors_count",
            query = "SELECT count(*) FROM ArchivedBuilds a WHERE a.temporaryBuild = false" +
                    " AND status = 'SYSTEM_ERROR'"),
    @NamedQuery(
            name = "ArchivedBuilds.system_errors_from_to_count",
            query = "SELECT count(*) FROM ArchivedBuilds a WHERE a.temporaryBuild = false" +
                    " AND status = 'SYSTEM_ERROR'" +
                    " AND startTime >= :from AND endTime <= :to")
})
public class ArchivedBuilds implements Serializable {
    @Id
    @Column(name = "buildrecord_id", unique = true, nullable = false)
    private int buildRecordId;

    @Column(name = "submittime")
    private Timestamp submitTime;

    @Column(name = "starttime")
    private Timestamp startTime;

    @Column(name = "endtime")
    private Timestamp endTime;

    @Column(name = "submit_year")
    private int submitYear;

    @Column(name = "submit_month")
    private int submitMonth;

    @Column(name = "submit_quarter")
    private int submitQuarter;

    @Column(name = "status")
    private String status;

    @Column(name = "buildtype")
    private String buildType;

    @Column(name = "executionrootname")
    private String executionRootName;

    @Column(name = "executionrootversion")
    private String executionRootVersion;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "temporarybuild")
    private boolean temporaryBuild;

    @Column(name = "autoalign")
    private boolean autoAlign;

    @Column(name = "trimmed_buildlog")
    private String trimmedBuildLog;

    @Column(name = "trimmed_repourlog")
    private String trimmedRepourLog;

    @Column(name = "categorized_error_msg")
    private String categorizedErrorMsg;

    @Column(name = "categorized_error_group")
    private String categorizedErrorGroup;

    @Column(name = "buildconfiguration_id", nullable = true)
    private int buildconfigurationId;

    @Column(name = "buildconfiguration_rev", nullable = true)
    private int buildconfigurationRev;

    @Column(name = "buildconfigsetrecord_id", nullable = false)
    private int buildconfigsetrecordId;

    @Column(name = "productmilestone_id", nullable = false)
    private int productmilestoneId;

    @Column(name = "project_id", nullable = false)
    private int projectId;

    @Column(name = "buildenvironment_id", nullable = false)
    private int buildenvironmentId;

    @Column(name = "productversion_id", nullable = false)
    private int productversionId;

    @Column(name = "product_id", nullable = false)
    private int productId;

    @Column(name = "brewpullactive")
    private boolean brewpullactive;


    public int getBuildRecordId() {
        return buildRecordId;
    }

    public void setBuildRecordId(int buildRecordId) {
        this.buildRecordId = buildRecordId;
    }

    public Timestamp getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Timestamp submitTime) {
        this.submitTime = submitTime;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public int getSubmitYear() {
        return submitYear;
    }

    public void setSubmitYear(int submitYear) {
        this.submitYear = submitYear;
    }

    public int getSubmitMonth() {
        return submitMonth;
    }

    public void setSubmitMonth(int submitMonth) {
        this.submitMonth = submitMonth;
    }

    public int getSubmitQuarter() {
        return submitQuarter;
    }

    public void setSubmitQuarter(int submitQuarter) {
        this.submitQuarter = submitQuarter;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBuildType() {
        return buildType;
    }

    public void setBuildType(String buildType) {
        this.buildType = buildType;
    }

    public String getExecutionRootName() {
        return executionRootName;
    }

    public void setExecutionRootName(String executionRootName) {
        this.executionRootName = executionRootName;
    }

    public String getExecutionRootVersion() {
        return executionRootVersion;
    }

    public void setExecutionRootVersion(String executionRootVersion) {
        this.executionRootVersion = executionRootVersion;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isTemporaryBuild() {
        return temporaryBuild;
    }

    public void setTemporaryBuild(boolean temporaryBuild) {
        this.temporaryBuild = temporaryBuild;
    }

    public boolean isAutoAlign() {
        return autoAlign;
    }

    public void setAutoAlign(boolean autoAlign) {
        this.autoAlign = autoAlign;
    }

    public String getTrimmedBuildLog() {
        return trimmedBuildLog;
    }

    public void setTrimmedBuildLog(String trimmedBuildLog) {
        this.trimmedBuildLog = trimmedBuildLog;
    }

    public String getTrimmedRepourLog() {
        return trimmedRepourLog;
    }

    public void setTrimmedRepourLog(String trimmedRepourLog) {
        this.trimmedRepourLog = trimmedRepourLog;
    }

    public String getCategorizedErrorMsg() {
        return categorizedErrorMsg;
    }

    public void setCategorizedErrorMsg(String categorizedErrorMsg) {
        this.categorizedErrorMsg = categorizedErrorMsg;
    }

    public String getCategorizedErrorGroup() {
        return categorizedErrorGroup;
    }

    public void setCategorizedErrorGroup(String categorizedErrorGroup) {
        this.categorizedErrorGroup = categorizedErrorGroup;
    }

    public int getBuildconfigurationId() {
        return buildconfigurationId;
    }

    public void setBuildconfigurationId(int buildconfigurationId) {
        this.buildconfigurationId = buildconfigurationId;
    }

    public int getBuildconfigurationRev() {
        return buildconfigurationRev;
    }

    public void setBuildconfigurationRev(int buildconfigurationRev) {
        this.buildconfigurationRev = buildconfigurationRev;
    }

    public int getBuildconfigsetrecordId() {
        return buildconfigsetrecordId;
    }

    public void setBuildconfigsetrecordId(int buildconfigsetrecordId) {
        this.buildconfigsetrecordId = buildconfigsetrecordId;
    }

    public int getProductmilestoneId() {
        return productmilestoneId;
    }

    public void setProductmilestoneId(int productmilestoneId) {
        this.productmilestoneId = productmilestoneId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getBuildenvironmentId() {
        return buildenvironmentId;
    }

    public void setBuildenvironmentId(int buildenvironmentId) {
        this.buildenvironmentId = buildenvironmentId;
    }

    public int getProductversionId() {
        return productversionId;
    }

    public void setProductversionId(int productversionId) {
        this.productversionId = productversionId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public boolean isBrewpullactive() {
        return brewpullactive;
    }

    public void setBrewpullactive(boolean brewpullactive) {
        this.brewpullactive = brewpullactive;
    }
}
