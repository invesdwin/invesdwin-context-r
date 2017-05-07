package de.invesdwin.context.r.runtime.jri;

import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.concurrent.NotThreadSafe;

import org.rosuda.JRI.Rengine;

import de.invesdwin.context.r.runtime.contract.IScriptTaskResults;

@NotThreadSafe
public class JriScriptTaskResults implements IScriptTaskResults {
    private final Rengine rengine;
    private final ReentrantLock rengineLock;

    public JriScriptTaskResults(final Rengine rengine, final ReentrantLock rengineLock) {
        this.rengine = rengine;
        this.rengineLock = rengineLock;
    }

    @Override
    public void close() {
        rengineLock.unlock();
    }

    @Override
    public Rengine getEngine() {
        return rengine;
    }

    @Override
    public String getString(final String variable) {
        return null;
    }

    @Override
    public String[] getStringVector(final String variable) {
        return null;
    }

    @Override
    public String[][] getStringMatrix(final String variable) {
        return null;
    }

    @Override
    public Double[] getDoubleVector(final String variable) {
        return null;
    }

    @Override
    public Double[][] getDoubleMatrix(final String variable) {
        return null;
    }

    @Override
    public Double getDouble(final String variable) {
        return null;
    }

    @Override
    public Boolean getBoolean(final String variable) {
        return null;
    }

    @Override
    public Boolean[] getBooleanVector(final String variable) {
        return null;
    }

    @Override
    public Boolean[][] getBooleanMatrix(final String variable) {
        return null;
    }

}