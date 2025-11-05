package com.zeromones.model;

import java.util.Arrays;
import java.util.List;

/**
 * Staff user roles with their associated permissions
 */
public enum StaffRole {
    ADMIN(
        "Administrator - Full system access",
        List.of(
            Permission.VIEW_ALL_BOOKINGS,
            Permission.MANAGE_BOOKINGS,
            Permission.VIEW_ALL_EMPLOYEES,
            Permission.MANAGE_EMPLOYEES,
            Permission.VIEW_ALL_WORKLISTS,
            Permission.MANAGE_WORKLISTS,
            Permission.ASSIGN_REQUESTS,
            Permission.VIEW_REPORTS
        )
    ),
    MANAGER(
        "Manager - Manage municipality operations",
        List.of(
            Permission.VIEW_MUNICIPALITY_BOOKINGS,
            Permission.MANAGE_BOOKINGS,
            Permission.VIEW_MUNICIPALITY_EMPLOYEES,
            Permission.VIEW_MUNICIPALITY_WORKLISTS,
            Permission.MANAGE_WORKLISTS,
            Permission.ASSIGN_REQUESTS
        )
    ),
    OPERATOR(
        "Operator - View and update bookings",
        List.of(
            Permission.VIEW_MUNICIPALITY_BOOKINGS,
            Permission.MANAGE_BOOKINGS,
            Permission.VIEW_MUNICIPALITY_WORKLISTS
        )
    );

    private final String description;
    private final List<Permission> permissions;

    StaffRole(String description, List<Permission> permissions) {
        this.description = description;
        this.permissions = permissions;
    }

    public String getDescription() {
        return description;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }

    public static StaffRole fromString(String role) {
        return Arrays.stream(values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElse(OPERATOR); // Default to most restrictive
    }

    /**
     * Permission enum defines all possible actions in the system
     */
    public enum Permission {
        // Booking permissions
        VIEW_ALL_BOOKINGS("View all bookings across all municipalities"),
        VIEW_MUNICIPALITY_BOOKINGS("View bookings from own municipality"),
        MANAGE_BOOKINGS("Create, update, and manage booking statuses"),

        // Employee permissions
        VIEW_ALL_EMPLOYEES("View all employees"),
        VIEW_MUNICIPALITY_EMPLOYEES("View employees from own municipality"),
        MANAGE_EMPLOYEES("Create, update, activate/deactivate employees"),

        // WorkList permissions
        VIEW_ALL_WORKLISTS("View all work lists"),
        VIEW_MUNICIPALITY_WORKLISTS("View work lists from own municipality"),
        MANAGE_WORKLISTS("Create, start, complete work lists"),
        ASSIGN_REQUESTS("Assign requests to work lists"),

        // Reports
        VIEW_REPORTS("View system reports and analytics");

        private final String description;

        Permission(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
