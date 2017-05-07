package de.invesdwin.context.r.runtime.contract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.springframework.core.io.ClassPathResource;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.Objects;

@NotThreadSafe
public class InputsAndResultsTestDouble {

    private final IScriptTaskRunner runner;

    public InputsAndResultsTestDouble(final IScriptTaskRunner runner) {
        this.runner = runner;
    }

    public void testDouble() {
        //putDouble
        final Double putDouble = 123.123D;
        final Double putDoubleNull = null;

        //putDoubleVector
        final Double[] putDoubleVector = new Double[3];
        for (int i = 0; i < putDoubleVector.length; i++) {
            putDoubleVector[i] = Double.parseDouble(i + "." + i);
        }
        final Double[] putDoubleVectorNull = Objects.clone(putDoubleVector);
        putDoubleVectorNull[1] = null;

        //putDoubleVectorAsList
        final List<Double> putDoubleVectorAsList = Arrays.asList(putDoubleVector);
        final List<Double> putDoubleVectorAsListNull = Objects.clone(putDoubleVectorAsList);
        putDoubleVectorAsListNull.set(1, null);

        //putDoubleMatrix
        final Double[][] putDoubleMatrix = new Double[3][];
        for (int i = 0; i < putDoubleMatrix.length; i++) {
            final Double[] vector = new Double[putDoubleMatrix.length];
            for (int j = 0; j < vector.length; j++) {
                vector[j] = Double.parseDouble(i + "" + j + "." + i + "" + j);
            }
            putDoubleMatrix[i] = vector;
        }
        final Double[][] putDoubleMatrixNull = Objects.clone(putDoubleMatrix);
        for (int i = 0; i < putDoubleMatrixNull.length; i++) {
            putDoubleMatrixNull[i][i] = null;
        }

        //putDoubleMatrixAsList
        final List<List<Double>> putDoubleMatrixAsList = new ArrayList<List<Double>>(putDoubleMatrix.length);
        for (final Double[] vector : putDoubleMatrix) {
            putDoubleMatrixAsList.add(Arrays.asList(vector));
        }
        final List<List<Double>> putDoubleMatrixAsListNull = Objects.clone(putDoubleMatrixAsList);
        for (int i = 0; i < putDoubleMatrixAsListNull.size(); i++) {
            putDoubleMatrixAsListNull.get(i).set(i, null);
        }

        final AScriptTask task = new AScriptTask(
                new ClassPathResource("InputsAndResultsTestDouble", InputsAndResultsTestDouble.class)) {
            @Override
            public void populateInputs(final IScriptTaskInputs inputs) {
                inputs.putDouble("putDouble", putDouble);
                inputs.putDouble("putDoubleNull", putDoubleNull);

                inputs.putDoubleVector("putDoubleVector", putDoubleVector);
                inputs.putDoubleVector("putDoubleVectorNull", putDoubleVectorNull);

                inputs.putDoubleVectorAsList("putDoubleVectorAsList", putDoubleVectorAsList);
                inputs.putDoubleVectorAsList("putDoubleVectorAsListNull", putDoubleVectorAsListNull);

                inputs.putDoubleMatrix("putDoubleMatrix", putDoubleMatrix);
                inputs.putDoubleMatrix("putDoubleMatrixNull", putDoubleMatrixNull);

                inputs.putDoubleMatrixAsList("putDoubleMatrixAsList", putDoubleMatrixAsList);
                inputs.putDoubleMatrixAsList("putDoubleMatrixAsListNull", putDoubleMatrixAsListNull);
            }
        };
        try (IScriptTaskResults results = runner.run(task)) {
            //getDouble
            final Double getDouble = results.getDouble("getDouble");
            Assertions.assertThat(putDouble).isEqualTo(getDouble);
            final Double getDoubleNull = results.getDouble("getDoubleNull");
            Assertions.assertThat(putDoubleNull).isEqualTo(getDoubleNull);

            //getDoubleVector
            final Double[] getDoubleVector = results.getDoubleVector("getDoubleVector");
            Assertions.assertThat(putDoubleVector).isEqualTo(getDoubleVector);
            final Double[] getDoubleVectorNull = results.getDoubleVector("getDoubleVectorNull");
            Assertions.assertThat(putDoubleVectorNull).isEqualTo(getDoubleVectorNull);

            //getDoubleVectorAsList
            final List<Double> getDoubleVectorAsList = results.getDoubleVectorAsList("getDoubleVectorAsList");
            Assertions.assertThat(putDoubleVectorAsList).isEqualTo(getDoubleVectorAsList);
            final List<Double> getDoubleVectorAsListNull = results.getDoubleVectorAsList("getDoubleVectorAsListNull");
            Assertions.assertThat(putDoubleVectorAsListNull).isEqualTo(getDoubleVectorAsListNull);

            //getDoubleMatrix
            final Double[][] getDoubleMatrix = results.getDoubleMatrix("getDoubleMatrix");
            Assertions.assertThat(putDoubleMatrix).isEqualTo(getDoubleMatrix);
            final Double[][] getDoubleMatrixNull = results.getDoubleMatrix("getDoubleMatrixNull");
            Assertions.assertThat(putDoubleMatrixNull).isEqualTo(getDoubleMatrixNull);

            //getDoubleMatrixAsList
            final List<List<Double>> getDoubleMatrixAsList = results.getDoubleMatrixAsList("getDoubleMatrixAsList");
            Assertions.assertThat(putDoubleMatrixAsList).isEqualTo(getDoubleMatrixAsList);
            final List<List<Double>> getDoubleMatrixAsListNull = results
                    .getDoubleMatrixAsList("getDoubleMatrixAsListNull");
            Assertions.assertThat(putDoubleMatrixAsListNull).isEqualTo(getDoubleMatrixAsListNull);
        }
    }

}
