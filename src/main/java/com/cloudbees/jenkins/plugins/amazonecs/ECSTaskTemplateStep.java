package com.cloudbees.jenkins.plugins.amazonecs;

import jenkins.model.*
import hudson.model.*;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.PrintStream;

public class ECSTaskTemplateStep extends Builder implements SimpleBuildStep {
    private String cloudName;
    private String instanceName;
    private String templateName;
    private String image;
    private String homeDir = "/home/jenkins";

    private PrintStream logger;

    @DataBoundConstructor
    public ECSTaskTemplateStep(String cloudName, String instanceName) {        
        this.instanceName = instanceName;
        this.templateName = "meeseeks-" + instanceName.replace(':', '-');
        this.image = "registry24.in.bbops.net/meeseeks/" + instanceName;
        this.homeDir = homeDir;
    }

    @DataBoundSetter
    public void setHomeDir(@Nonnull String homeDir) {
        this.homeDir = homeDir;
    }

    public String getHomeDir() {
        return homeDir;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public String getTemplateName() {
        return templateName;
    }

    public String getImage() {
        return image;
    }

    @Override
    public void perform(Run build, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {
        logger = listener.getLogger();
        if ()
        ECSTaskTemplate ecsTemplate = new ECSTaskTemplate(
            templateName,
            instanceName,
            image,
            "/home/jenkins",
            512,
            0,
            1,
            false,
            null,
            null,
            null,
            null
        );
        logger.println(ecsTemplate.getTemplateName());
        logger.println("Created new ECS template: ")
    }

    @Symbol("ecsTemplate")
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
