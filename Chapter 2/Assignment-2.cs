using System;
using System.Numerics;
class MyClass {
    static int[] getElementAndQueryCount() {
        return Array.ConvertAll(Console.ReadLine().Split(' '), int.Parse);
    }
    static long[] getArrayElements() {
        return Array.ConvertAll(Console.ReadLine().Split(' '), long.Parse);
    }
    static int[] getSubarraySize() {
        return Array.ConvertAll(Console.ReadLine().Split(' '), int.Parse);
    }
    static long[] getPrefixSumArray(long[] array, int arraySize) {
        long[] prefixArray = new long[arraySize + 1];
        prefixArray[0] = 0;
        for (int index = 1; index <= arraySize; index++)
        {
            prefixArray[index] = prefixArray[index - 1] + array[index - 1];
        }
        return prefixArray;
    }
    static long calculateMeanOfSubarray(long[] prefixSumArray, int arrayLeftLimit, int arrayRightLimit) {
        return (long)((long)(prefixSumArray[arrayRightLimit] - prefixSumArray[arrayLeftLimit - 1]) / (arrayRightLimit - arrayLeftLimit + 1));
    }
    static void processQuery(int queryCount, long[] prefixSumArray) {
        for (var x = 0; x < queryCount; x++)
        {
            var subarraySize = getSubarraySize();
            var meanOfSubarray = calculateMeanOfSubarray(prefixSumArray, subarraySize[0], subarraySize[1]);
            Console.WriteLine(meanOfSubarray);
        }
    }
    static void Main(string[] args) {
        var NQ = getElementAndQueryCount();
        var arr = getArrayElements();
        long[] sumarr = getPrefixSumArray(arr, NQ[0]);
        processQuery(NQ[1], sumarr);
    }
}