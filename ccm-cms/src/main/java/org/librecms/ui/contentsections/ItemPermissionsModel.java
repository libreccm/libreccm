/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ItemPermissionsModel {

    private boolean grantedAdminister;

    private boolean grantedApplyAlternateWorkflow;

    private boolean grantedApprove;

    private boolean grantedCategorize;

    private boolean grantedCreateNew;

    private boolean grantedDelete;

    private boolean grantedEdit;

    private boolean grantedPreview;

    private boolean grantedPublish;

    private boolean grantedViewPublished;

    public boolean isGrantedAdminister() {
        return grantedAdminister;
    }

    public void setGrantedAdminister(final boolean grantedAdminister) {
        this.grantedAdminister = grantedAdminister;
    }

    public boolean isGrantedApplyAlternateWorkflow() {
        return grantedApplyAlternateWorkflow;
    }

    public void setGrantedApplyAlternateWorkflow(
        boolean grantedApplyAlternateWorkflow) {
        this.grantedApplyAlternateWorkflow = grantedApplyAlternateWorkflow;
    }

    public boolean isGrantedApprove() {
        return grantedApprove;
    }

    public void setGrantedApprove(final boolean grantedApprove) {
        this.grantedApprove = grantedApprove;
    }

    public boolean isGrantedCategorize() {
        return grantedCategorize;
    }

    public void setGrantedCategorize(final boolean grantedCategorize) {
        this.grantedCategorize = grantedCategorize;
    }

    public boolean isGrantedCreateNew() {
        return grantedCreateNew;
    }

    public void setGrantedCreateNew(final boolean grantedCreateNew) {
        this.grantedCreateNew = grantedCreateNew;
    }

    public boolean isGrantedDelete() {
        return grantedDelete;
    }

    public void setGrantedDelete(final boolean grantedDelete) {
        this.grantedDelete = grantedDelete;
    }

    public boolean isGrantedEdit() {
        return grantedEdit;
    }

    public void setGrantedEdit(final boolean grantedEdit) {
        this.grantedEdit = grantedEdit;
    }

    public boolean isGrantedPreview() {
        return grantedPreview;
    }

    public void setGrantedPreview(final boolean grantedPreview) {
        this.grantedPreview = grantedPreview;
    }

    public boolean isGrantedPublish() {
        return grantedPublish;
    }

    public void setGrantedPublish(final boolean grantedPublish) {
        this.grantedPublish = grantedPublish;
    }

    public boolean isGrantedViewPublished() {
        return grantedViewPublished;
    }

    public void setGrantedViewPublished(final boolean grantedViewPublished) {
        this.grantedViewPublished = grantedViewPublished;
    }

}
