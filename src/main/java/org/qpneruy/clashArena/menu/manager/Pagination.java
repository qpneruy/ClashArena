package org.qpneruy.clashArena.menu.manager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Pagination<T> {

    /**
     * Paginates a list in reverse order.
     *
     * @param list         The input list to paginate.
     * @param inventorySize The size of each page.
     * @param page         The page number (1-based).
     * @return A sublist containing elements for the specified page in reverse order.
     */
    public List<T> paginateReverse(List<T> list, int inventorySize, int page) {
        if (page <= 0) {
            page = 1;
        }

        if (inventorySize <= 0) {
            throw new IllegalArgumentException("inventorySize must be greater than 0.");
        }

        int idStart = Math.max(0, list.size() - (page - 1) * inventorySize - 1);
        int idEnd = Math.max(-1, idStart - inventorySize);
        List<T> currentList = new ArrayList<>();

        for (int i = idStart; i > idEnd; --i) {
            currentList.add(list.get(i));
        }

        return currentList;
    }

    /**
     * Paginates a list in normal order.
     *
     * @param list The input list to paginate.
     * @param size The size of each page.
     * @param page The page number (1-based).
     * @return A sublist containing elements for the specified page.
     */
    public List<T> paginate(List<T> list, int size, int page) {
        if (page <= 0) {
            page = 1;
        }

        if (size <= 0) {
            throw new IllegalArgumentException("size must be greater than 0.");
        }

        int idStart = (page - 1) * size;
        int idEnd = Math.min(list.size(), idStart + size);
        return new ArrayList<>(list.subList(idStart, idEnd));
    }

    /**
     * Paginates a map in reverse order.
     *
     * @param map         The input map to paginate.
     * @param inventorySize The size of each page.
     * @param page        The page number (1-based).
     * @return A list containing values for the specified page in reverse order.
     */
    public List<T> paginateReverse(Map<?, T> map, int inventorySize, int page) {
        return paginateReverse(new ArrayList<>((new LinkedHashMap<>(map)).values()), inventorySize, page);
    }

    /**
     * Paginates a map in normal order.
     *
     * @param map  The input map to paginate.
     * @param size The size of each page.
     * @param page The page number (1-based).
     * @return A list containing values for the specified page.
     */
    public List<T> paginate(Map<?, T> map, int size, int page) {
        return paginate(new ArrayList<>((new LinkedHashMap<>(map)).values()), size, page);
    }
}
