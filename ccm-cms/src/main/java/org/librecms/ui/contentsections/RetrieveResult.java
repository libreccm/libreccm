/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import java.util.Objects;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T>
 */
public class RetrieveResult<T> {

    private T result;

    private String responseTemplate;

    private boolean successful;

    private RetrieveResult() {

    }

    public static <R> RetrieveResult<R> successful(final R result) {
        final RetrieveResult<R> retrieveResult = new RetrieveResult<>();
        retrieveResult.setResult(Objects.requireNonNull(result));
        retrieveResult.setSuccessful(true);
        return retrieveResult;
    }

    public static <R> RetrieveResult<R> failed(final String responseTemplate) {
        final RetrieveResult<R> retrieveResult = new RetrieveResult<>();
        retrieveResult.setResponseTemplate(
            Objects.requireNonNull(responseTemplate)
        );
        retrieveResult.setSuccessful(false);
        return retrieveResult;
    }

    public T getResult() {
        return result;
    }

    private void setResult(final T result) {
        this.result = result;
    }

    public String getResponseTemplate() {
        return responseTemplate;
    }

    private void setResponseTemplate(final String responseTemplate) {
        this.responseTemplate = responseTemplate;
    }

    public boolean isSuccessful() {
        return successful;
    }

    private void setSuccessful(final boolean successful) {
        this.successful = successful;
    }

}
