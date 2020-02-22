/**
 * MIT License
 *
 * Copyright (c) 2010 - 2020 The OSHI Project Contributors: https://github.com/oshi/oshi/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package oshi.driver.windows.perfmon;

import java.util.List;
import java.util.Map;

import oshi.util.platform.windows.PerfCounterQuery;
import oshi.util.platform.windows.PerfCounterWildcardQuery;
import oshi.util.platform.windows.PerfCounterWildcardQuery.PdhCounterWildcardProperty;
import oshi.util.tuples.Pair;

public class ProcessInformation {

    private static final String WIN32_PROCESS = "Win32_Process";
    private static final String PROCESS = "Process";
    private static final String PROCESS_INFORMATION = "Process Information";
    private static final String WIN32_PROCESS_WHERE_NOT_NAME_LIKE_TOTAL = "Win32_Process WHERE NOT Name LIKE\"%_Total\"";

    /**
     * Process performance counters
     */
    public enum ProcessPerformanceProperty implements PdhCounterWildcardProperty {
        // First element defines WMI instance name field and PDH instance filter
        NAME(PerfCounterQuery.NOT_TOTAL_INSTANCES),
        // Remaining elements define counters
        PRIORITY("Priority Base"), //
        CREATIONDATE("Elapsed Time"), //
        PROCESSID("ID Process"), //
        PARENTPROCESSID("Creating Process ID"), //
        READTRANSFERCOUNT("IO Read Bytes/sec"), //
        WRITETRANSFERCOUNT("IO Write Bytes/sec"), //
        PRIVATEPAGECOUNT("Working Set - Private");

        private final String counter;

        ProcessPerformanceProperty(String counter) {
            this.counter = counter;
        }

        @Override
        public String getCounter() {
            return counter;
        }
    }

    /**
     * Handle performance counters
     */
    public enum HandleCountProperty implements PdhCounterWildcardProperty {
        // First element defines WMI instance name field and PDH instance filter
        NAME(PerfCounterQuery.TOTAL_INSTANCE),
        // Remaining elements define counters
        HANDLECOUNT("Handle Count");

        private final String counter;

        HandleCountProperty(String counter) {
            this.counter = counter;
        }

        @Override
        public String getCounter() {
            return counter;
        }
    }

    /**
     * Returns process counters.
     *
     * @return Process counters for each process.
     */
    public Pair<List<String>, Map<ProcessPerformanceProperty, List<Long>>> queryProcessCounters() {
        PerfCounterWildcardQuery<ProcessPerformanceProperty> processPerformancePerfCounters = new PerfCounterWildcardQuery<>(
                ProcessPerformanceProperty.class, PROCESS, WIN32_PROCESS_WHERE_NOT_NAME_LIKE_TOTAL,
                PROCESS_INFORMATION);
        Map<ProcessPerformanceProperty, List<Long>> values = processPerformancePerfCounters.queryValuesWildcard();
        List<String> instances = processPerformancePerfCounters.getInstancesFromLastQuery();
        return new Pair<>(instances, values);
    }

    /**
     * Returns handle counters
     *
     * @return Process handle counters
     */
    public Map<HandleCountProperty, List<Long>> queryHandles() {
        PerfCounterWildcardQuery<HandleCountProperty> handlePerfCounters = new PerfCounterWildcardQuery<>(
                HandleCountProperty.class, PROCESS, WIN32_PROCESS);
        return handlePerfCounters.queryValuesWildcard();
    }
}