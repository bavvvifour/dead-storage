package com.storage.bavifour.utils.BreadcrumbsUtil;

import java.util.ArrayList;
import java.util.List;

public class BreadcrumbsUtil {

    public static List<Breadcrumb> generateBreadcrumbs(String path) {
        List<Breadcrumb> breadcrumbs = new ArrayList<>();
        String[] parts = path.split("/");
        StringBuilder currentPath = new StringBuilder();

        for (String part : parts) {
            if (!part.isEmpty()) {
                if (!currentPath.isEmpty()) {
                    currentPath.append("/");
                }
                currentPath.append(part);
                breadcrumbs.add(new Breadcrumb(part, currentPath.toString()));
            }
        }

        return breadcrumbs;
    }
}
