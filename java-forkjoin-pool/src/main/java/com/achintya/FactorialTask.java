package com.achintya;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.IntStream;

@AllArgsConstructor
@RequiredArgsConstructor
public class FactorialTask extends RecursiveTask<BigInteger> {
    private int start = 1;
    private final int n;
    private static final int THRESHOLD = 20;

    /**
     * The main computation performed by this task.
     *
     * @return the result of the computation
     */
    @Override
    protected BigInteger compute() {
        if ((n - start) >= THRESHOLD) {
            return ForkJoinTask.invokeAll(createSubTasks())
                    .stream()
                    .map(a -> a.join())
                    .reduce(BigInteger.ONE, BigInteger::multiply);
        } else {
            return calculate(start, n);
        }
    }

    private Collection<FactorialTask> createSubTasks() {
        val dividedTasks = new ArrayList<FactorialTask>();
        int mid = (start + n) / 2;
        dividedTasks.add(new FactorialTask(start, mid));
        dividedTasks.add(new FactorialTask(++mid, n));
        return dividedTasks;
    }

    private BigInteger calculate(int start, int n) {
        return IntStream.rangeClosed(start, n)
                .mapToObj(BigInteger::valueOf)
                .reduce(BigInteger.ONE, BigInteger::multiply);
    }

    public static void main(String[] args) {
        val pool = ForkJoinPool.commonPool();
        val result = pool.invoke(new FactorialTask(100));
        System.out.println(result);
    }
}
