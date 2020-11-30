package it.unibo.oop.lab.workers02;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a standard implementation of the calculation.
 * 
 */
public class MultiThreadedSumMatrix implements SumMatrix {
    private final int nthread;
    /**
     * 
     * @param nthread
     *            no. of thread performing the sum.
     */
    public MultiThreadedSumMatrix(final int nthread) {
        this.nthread = nthread;
    }
    private static class Worker extends Thread {
        private final int index;
        private final int size;
        private final double[][] matrix;
        private long res;

        /**
         * Build a new worker.
         * 
         * @param matrix
         *            the matrix to sum
         * @param index
         *            the initial position for this worker
         * @param size
         *            the no. of arrays to sum up for this worker
         */
        Worker(final double[][] matrix, final int index, final int size) {
            this.matrix = matrix.clone();
            this.index = index;
            this.size = size;
        }

        @Override
        public void run() {
            int finalIndexValue = this.index + this.size;
            if (finalIndexValue > matrix.length) {
                finalIndexValue = matrix.length;
            }
            System.out.println("Working from matrix[" + this.index + "][] to matrix[" + (finalIndexValue - 1) + "][]");
            for (int i = index; i < finalIndexValue; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    this.res += matrix[i][j];
                }
            }
        }

        /**
         * Returns the result of summing up the integers within the matrix.
         * 
         * @return the sum of every element in the matrix
         */
        public long getResult() {
            return this.res;
        }

    }

    @Override
    public double sum(final double[][] matrix) {
        /*
         * Build a list of workers
         */
        final int size = matrix.length % nthread + matrix.length / nthread;
        final List<Worker> workers = new ArrayList<>(nthread);
        for (int i = 0; i < matrix.length; i += size) {
            workers.add(new Worker(matrix, i, size));
        }
        for (final Worker w: workers) {
            w.start();
        }
        /*
         * Wait for every one of them to finish. This operation is _way_ better done by
         * using barriers and latches, and the whole operation would be better done with
         * futures.
         */
        long sum = 0;
        for (final Worker w: workers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        /*
         * Return the sum
         */
        return sum;
    }

}
