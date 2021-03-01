/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import java.util.Objects;

/**
 * This class can be used by controller methods to either return an entity, for
 * example a content section, or the path to an error response if the identity
 * for the provided identifier was not found.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T> The entity class contained by an instance of this class if it was
 *            retrived successfully.
 */
public class RetrieveResult<T> {

    /**
     * The retrieved entity if the it was retrieved successfully.
     */
    private T result;

    /**
     * The path of the response template to show when the entity was <em>no</em>
     * retrived successfully.
     */
    private String failedResponseTemplate;

    /**
     * Indicates if the entity was retrieved succesfully.
     */
    private boolean successful;

    private RetrieveResult() {

    }

    /**
     * Creates a successful {@code RetrieveResult} instance.
     *
     * @param <R>    The type of the retrieved entity.
     * @param result The retrieved entity. Can't be null.
     *
     * @return The new {@code RetrieveResult}.
     */
    public static <R> RetrieveResult<R> successful(final R result) {
        final RetrieveResult<R> retrieveResult = new RetrieveResult<>();
        retrieveResult.setResult(Objects.requireNonNull(result));
        retrieveResult.setSuccessful(true);
        return retrieveResult;
    }

    /**
     * Creates a failed {@code RetrieveResult} instance.
     *
     * @param <R>              The type of the entity to retrieve.
     * @param responseTemplate The template to show.
     *
     * @return The new {@code RetrieveResult}.
     */
    public static <R> RetrieveResult<R> failed(final String responseTemplate) {
        final RetrieveResult<R> retrieveResult = new RetrieveResult<>();
        retrieveResult.setFailedResponseTemplate(
            Objects.requireNonNull(responseTemplate)
        );
        retrieveResult.setSuccessful(false);
        return retrieveResult;
    }

    /**
     * Gets the result.
     *
     * @return The retrieved entity.
     */
    public T getResult() {
        return result;
    }

    /**
     * Private setter for the result entity.
     *
     * @param result The retrieved entity.
     */
    private void setResult(final T result) {
        this.result = result;
    }

    /**
     * Gets the failed result template.
     *
     * @return The path of the template to show.
     */
    public String getFailedResponseTemplate() {
        return failedResponseTemplate;
    }

    /**
     * Private setter for the failed response template.
     *
     * @param failedResponseTemplate Path of the template to show.
     */
    private void setFailedResponseTemplate(final String failedResponseTemplate) {
        this.failedResponseTemplate = failedResponseTemplate;
    }

    /**
     * Indicates if the entity was retrieve succesfully.
     *
     * @return {@code true} if the enity was retrieved successfully,
     *         {@code false} otherwise.
     */
    public boolean isSuccessful() {
        return successful;
    }

    /**
     * Private setter for {@link #successful}.
     *
     * @param successful {@code true} if the enity was retrieved successfully,
     *                   {@code false} otherwise.
     */
    private void setSuccessful(final boolean successful) {
        this.successful = successful;
    }

}
