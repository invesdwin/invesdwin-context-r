package de.invesdwin.context.r.runtime.contract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.springframework.core.io.ClassPathResource;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.Objects;

@NotThreadSafe
public class InputsAndResultsTestBoolean {

    private final IScriptTaskRunner runner;

    public InputsAndResultsTestBoolean(final IScriptTaskRunner runner) {
        this.runner = runner;
    }

    public void testBoolean() {
        //putBoolean
        final Boolean putBoolean = true;
        final Boolean putBooleanNull = null;

        //putBooleanVector
        final Boolean[] putBooleanVector = new Boolean[3];
        for (int i = 0; i < putBooleanVector.length; i++) {
            putBooleanVector[i] = i % 2 == 0;
        }
        final Boolean[] putBooleanVectorNull = Objects.clone(putBooleanVector);
        putBooleanVectorNull[1] = null;

        //putBooleanVectorAsList
        final List<Boolean> putBooleanVectorAsList = Arrays.asList(putBooleanVector);
        final List<Boolean> putBooleanVectorAsListNull = Objects.clone(putBooleanVectorAsList);
        putBooleanVectorAsListNull.set(1, null);

        //putBooleanMatrix
        final Boolean[][] putBooleanMatrix = new Boolean[3][];
        for (int i = 0; i < putBooleanMatrix.length; i++) {
            final Boolean[] vector = new Boolean[putBooleanMatrix.length];
            for (int j = 0; j < vector.length; j++) {
                vector[j] = j % 2 == 0;
            }
            putBooleanMatrix[i] = vector;
        }
        final Boolean[][] putBooleanMatrixNull = Objects.clone(putBooleanMatrix);
        for (int i = 0; i < putBooleanMatrixNull.length; i++) {
            putBooleanMatrixNull[i][i] = null;
        }

        //putBooleanMatrixAsList
        final List<List<Boolean>> putBooleanMatrixAsList = new ArrayList<List<Boolean>>(putBooleanMatrix.length);
        for (final Boolean[] vector : putBooleanMatrix) {
            putBooleanMatrixAsList.add(Arrays.asList(vector));
        }
        final List<List<Boolean>> putBooleanMatrixAsListNull = Objects.clone(putBooleanMatrixAsList);
        for (int i = 0; i < putBooleanMatrixAsListNull.size(); i++) {
            putBooleanMatrixAsListNull.get(i).set(i, null);
        }

        final AScriptTask task = new AScriptTask(
                new ClassPathResource("InputsAndResultsTestBoolean", InputsAndResultsTestBoolean.class)) {
            @Override
            public void populateInputs(final IScriptTaskInputs inputs) {
                inputs.putBoolean("putBoolean", putBoolean);
                inputs.putBoolean("putBooleanNull", putBooleanNull);

                inputs.putBooleanVector("putBooleanVector", putBooleanVector);
                inputs.putBooleanVector("putBooleanVectorNull", putBooleanVectorNull);

                inputs.putBooleanVectorAsList("putBooleanVectorAsList", putBooleanVectorAsList);
                inputs.putBooleanVectorAsList("putBooleanVectorAsListNull", putBooleanVectorAsListNull);

                inputs.putBooleanMatrix("putBooleanMatrix", putBooleanMatrix);
                inputs.putBooleanMatrix("putBooleanMatrixNull", putBooleanMatrixNull);

                inputs.putBooleanMatrixAsList("putBooleanMatrixAsList", putBooleanMatrixAsList);
                inputs.putBooleanMatrixAsList("putBooleanMatrixAsListNull", putBooleanMatrixAsListNull);
            }
        };
        try (IScriptTaskResults results = runner.run(task)) {
            //getBoolean
            final Boolean getBoolean = results.getBoolean("getBoolean");
            Assertions.assertThat(putBoolean).isEqualTo(getBoolean);
            final Boolean getBooleanNull = results.getBoolean("getBooleanNull");
            Assertions.assertThat(putBooleanNull).isEqualTo(getBooleanNull);

            //getBooleanVector
            final Boolean[] getBooleanVector = results.getBooleanVector("getBooleanVector");
            Assertions.assertThat(putBooleanVector).isEqualTo(getBooleanVector);
            final Boolean[] getBooleanVectorNull = results.getBooleanVector("getBooleanVectorNull");
            Assertions.assertThat(putBooleanVectorNull).isEqualTo(getBooleanVectorNull);

            //getBooleanVectorAsList
            final List<Boolean> getBooleanVectorAsList = results.getBooleanVectorAsList("getBooleanVectorAsList");
            Assertions.assertThat(putBooleanVectorAsList).isEqualTo(getBooleanVectorAsList);
            final List<Boolean> getBooleanVectorAsListNull = results
                    .getBooleanVectorAsList("getBooleanVectorAsListNull");
            Assertions.assertThat(putBooleanVectorAsListNull).isEqualTo(getBooleanVectorAsListNull);

            //getBooleanMatrix
            final Boolean[][] getBooleanMatrix = results.getBooleanMatrix("getBooleanMatrix");
            Assertions.assertThat(putBooleanMatrix).isEqualTo(getBooleanMatrix);
            final Boolean[][] getBooleanMatrixNull = results.getBooleanMatrix("getBooleanMatrixNull");
            Assertions.assertThat(putBooleanMatrixNull).isEqualTo(getBooleanMatrixNull);

            //getBooleanMatrixAsList
            final List<List<Boolean>> getBooleanMatrixAsList = results.getBooleanMatrixAsList("getBooleanMatrixAsList");
            Assertions.assertThat(putBooleanMatrixAsList).isEqualTo(getBooleanMatrixAsList);
            final List<List<Boolean>> getBooleanMatrixAsListNull = results
                    .getBooleanMatrixAsList("getBooleanMatrixAsListNull");
            Assertions.assertThat(putBooleanMatrixAsListNull).isEqualTo(getBooleanMatrixAsListNull);
        }
    }

}
