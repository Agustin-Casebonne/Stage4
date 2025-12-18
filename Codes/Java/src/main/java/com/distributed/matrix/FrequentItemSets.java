package com.distributed.matrix;
import java.util.*;
import java.util.stream.Collectors;

public class FrequentItemSets {

    // Soporte mínimo (min_sup) - Requisito del problema
    private static final int MIN_SUPPORT = 2; 

    // Clase para simular el contexto de escritura
    static class Context {
        public void write(String key, int value) {
            // Emite (item, 1) en el Mapper, o (item, count) en el Reducer
            System.out.println("Output: Key=" + key + ", Value=" + value);
        }
    }

    // ================= MAPPER CLASS (Cuenta de Ítems) =================
    public static class ItemCountMapper {
        /**
         * Recibe una transacción (línea) y emite (Item, 1) por cada ítem.
         * Formato de entrada: <ID_Transaccion> <Item1,Item2,Item3,...>
         * Ejemplo: T1 Leche,Pan,Huevos
         */
        public List<Map.Entry<String, Integer>> map(String transactionLine) {
            List<Map.Entry<String, Integer>> intermediatePairs = new ArrayList<>();
            // Separamos solo los ítems (asumiendo que los ítems están separados por coma)
            String[] parts = transactionLine.split(" ")[1].split(","); 
            
            for (String item : parts) {
                // Emite (Item, 1)
                intermediatePairs.add(new AbstractMap.SimpleEntry<>(item.trim(), 1));
            }
            return intermediatePairs;
        }
    }

    // ================= REDUCER CLASS (Filtro por Soporte) =================
    public static class SupportReducer {
        /**
         * Recibe un Item y la lista de conteos (todos '1').
         * Calcula el soporte total y filtra por MIN_SUPPORT.
         */
        public void reduce(String item, List<Integer> counts, Context context) {
            int totalCount = counts.stream().mapToInt(Integer::intValue).sum();
            
            if (totalCount >= MIN_SUPPORT) {
                // Si el soporte es suficiente, es un Ítem Frecuente (L1)
                context.write(item, totalCount);
            }
        }
    }

    public static void main(String[] args) {
        // --- SIMULACIÓN DE DATOS DE ENTRADA (Transacciones) ---
        List<String> transactions = Arrays.asList(
            "T1 Leche,Pan,Huevos",
            "T2 Pan,Cereal,Jugo",
            "T3 Leche,Pan,Queso",
            "T4 Cereal,Jugo",
            "T5 Pan,Queso,Huevos"
        );

        System.out.println("--- FASE MAP: Conteo de (Item, 1) ---");
        ItemCountMapper mapper = new ItemCountMapper();
        
        // 1. Ejecución de los mappers y recolección de pares intermedios
        List<Map.Entry<String, Integer>> allIntermediatePairs = transactions.stream()
            .flatMap(line -> mapper.map(line).stream())
            .collect(Collectors.toList());

        System.out.println("Pares Intermedios:");
        allIntermediatePairs.forEach(e -> System.out.println("Key: " + e.getKey() + ", Value: " + e.getValue()));

        System.out.println("\n--- FASE SHUFFLE & SORT: Agrupación por Item ---");
        // 2. Agrupación (Shuffle y Sort)
        Map<String, List<Integer>> groupedData = allIntermediatePairs.stream()
            .collect(Collectors.groupingBy(Map.Entry::getKey, 
                                           Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
        
        System.out.println("Datos Agrupados: " + groupedData.keySet());

        System.out.println("\n--- FASE REDUCE: Determinación de L1 (Items Frecuentes) ---");
        SupportReducer reducer = new SupportReducer();
        Context context = new Context();
        
        // 3. Ejecución de los reducers
        for (Map.Entry<String, List<Integer>> entry : groupedData.entrySet()) {
            reducer.reduce(entry.getKey(), entry.getValue(), context);
        }
        
        System.out.println("\nL1 (Items Frecuentes con Soporte >= " + MIN_SUPPORT + ") calculada exitosamente.");
    }
}