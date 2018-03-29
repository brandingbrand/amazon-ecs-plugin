package com.cloudbees.jenkins.plugins.amazonecs;

import hudson.model.*;
import jenkins.model.*;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.slaves.Cloud;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

public class ECSTaskTemplateStep extends Builder implements SimpleBuildStep {

    private String cloudName;
    private ECSTaskTemplate template;
    private ECSCloud cloud;
    private List<ECSTaskTemplate> templates;
    private PrintStream logger;

    @DataBoundConstructor
    public ECSTaskTemplateStep(
        @Nonnull String cloudName,
        @Nonnull String templateName,
        @Nullable String label,
        @Nonnull String image,
        @Nullable String remoteFSRoot,
        int memory,
        int memoryReservation,
        int cpu,
        boolean privileged,
        @Nullable List<ECSTaskTemplate.LogDriverOption> logDriverOptions,
        @Nullable List<ECSTaskTemplate.EnvironmentEntry> environments,
        @Nullable List<ECSTaskTemplate.ExtraHostEntry> extraHosts,
        @Nullable List<ECSTaskTemplate.MountPointEntry> mountPoints) {
            this.cloudName = cloudName;
            this.template = new ECSTaskTemplate(
                templateName,
                label,
                image,
                remoteFSRoot,
                memory,
                memoryReservation,
                cpu,
                privileged,
                logDriverOptions,
                environments,
                extraHosts,
                mountPoints);
           this.cloud = (ECSCloud) Jenkins.getInstance().clouds.getByName(cloudName);
           this.templates = cloud.getTemplates();
    }

    @DataBoundSetter
    public void setTaskrole(String taskRoleArn) {
        template.setTaskrole(StringUtils.trimToNull(taskRoleArn));
    }

    public String getTaskrole() {
        return template.getTaskrole();
    }

    @DataBoundSetter
    public void setEntrypoint(String entrypoint) {
        template.setEntrypoint(StringUtils.trimToNull(entrypoint));
    }

    public String getEntrypoint() {
        return template.getEntrypoint();
    }

    @DataBoundSetter
    public void setJvmArgs(String jvmArgs) {
        template.setJvmArgs(StringUtils.trimToNull(jvmArgs));
    }

    public String getJvmArgs() {
        return template.getJvmArgs();
    }

    @DataBoundSetter
    public void setLogDriver(String logDriver) {
        template.setLogDriver(StringUtils.trimToNull(logDriver));
    }

    public String getLogDriver() {
        return template.getLogDriver();
    }

    @DataBoundSetter
    public void setDnsSearchDomains(String dnsSearchDomains) {
        template.setDnsSearchDomains(StringUtils.trimToNull(dnsSearchDomains));
    }

    public String getDnsSearchDomains() {
        return template.getDnsSearchDomains();
    }

    public String getCloudName() {
        return cloudName;
    }

    public String getTemplateName() {
        return template.getTemplateName();
    }

    public String getLabel() {
        return template.getLabel();
    }

    public String getImage() {
        return template.getImage();
    }

    public String getRemoteFSRoot() {
        return template.getRemoteFSRoot();
    }

    public int getMemory() {
        return template.getMemory();
    }

    public int getMemoryReservation() {
        return template.getMemoryReservation();
    }

    public int getCpu() {
        return template.getCpu();
    }

    public boolean getPrivileged() {
        return template.getPrivileged();
    }

    public List<ECSTaskTemplate.LogDriverOption> getLogDriverOptions() {
        return template.getLogDriverOptions();
    }

    public List<ECSTaskTemplate.EnvironmentEntry> getEnvironments() {
        return template.getEnvironments();
    }

    public List<ECSTaskTemplate.ExtraHostEntry> getExtraHosts() {
        return template.getExtraHosts();
    }

    public List<ECSTaskTemplate.MountPointEntry> getMountPoints() {
        return template.getMountPoints();
    }

    @Override
    public void perform(Run build, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {
        logger = listener.getLogger();
        logger.println(templates);
        if (templates.isEmpty()) {
            templates.add(template);
        } else {
            boolean templateExists = false;
            for (ECSTaskTemplate t : templates) {
                if(t.getLabel().equals(getLabel())) {
                    logger.println("ECS template: " + getLabel() + " already exists, skipping step");
                    templateExists = true;
                }
            }
            if (!templateExists) {
                logger.println("Created new ECS template: " + getLabel());
                templates.add(template);
                logger.println("Saving Jenkins");
                Jenkins.getInstance().save();
            }
        }
    }

    @Symbol("ecsTaskTemplate")
    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Builder> {

        @Override
        public String getDisplayName() {
            return "Create new ECS template";
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> t) {
            return true;
        }
    }
}
