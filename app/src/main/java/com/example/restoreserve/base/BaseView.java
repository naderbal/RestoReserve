
package com.example.restoreserve.base;

/**
 * Base View to be implemented by all Views (MVP) in app.
 * @param <T> Generic class of the presenter to be set.
 */
public interface BaseView<T> {

    /**
     * Implemented to set presenter.
     * @param presenter the presenter to be set.
     */
    void setPresenter(T presenter);

}
