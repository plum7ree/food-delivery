//package com.example.driverassignment;
//
//import java.util.Arrays;
//
//public class HungarianAlgorithm {
//
//    public static int[] apply(int[][] costMatrix) {
//        int n = costMatrix.length;
//        int[] assignment = new int[n];
//        Arrays.fill(assignment, -1);
//
//        int[] u = new int[n]; // potential for rows
//        int[] v = new int[n]; // potential for columns
//        int[] p = new int[n]; // the matching
//
//        for (int i = 0; i < n; i++) {
//            int[] links = new int[n];
//            int[] mins = new int[n];
//            boolean[] visited = new boolean[n];
//            Arrays.fill(mins, Integer.MAX_VALUE);
//            Arrays.fill(links, -1);
//            int markedI = i, markedJ = -1, j;
//            while (markedI != -1) {
//                j = -1;
//                for (int k = 0; k < n; k++) {
//                    if (!visited[k]) {
//                        int cur = costMatrix[markedI][k] - u[markedI] - v[k];
//                        if (cur < mins[k]) {
//                            mins[k] = cur;
//                            links[k] = markedJ;
//                        }
//                        if (j == -1 || mins[k] < mins[j]) {
//                            j = k;
//                        }
//                    }
//                }
//                int delta = mins[j];
//                for (int k = 0; k < n; k++) {
//                    if (visited[k]) {
//                        u[p[k]] += delta;
//                        v[k] -= delta;
//                    } else {
//                        mins[k] -= delta;
//                    }
//                }
//                u[i] += delta;
//                visited[j] = true;
//                markedJ = j;
//                markedI = p[j];
//            }
//            while (links[j] != -1) {
//                p[j] = p[links[j]];
//                j = links[j];
//            }
//            p[j] = i;
//        }
//
//        for (int j = 0; j < n; j++) {
//            assignment[p[j]] = j;
//        }
//
//        return assignment;
//    }
//}
