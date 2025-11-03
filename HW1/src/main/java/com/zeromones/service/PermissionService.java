package com.zeromones.service;

import com.zeromones.model.StaffRole;
import com.zeromones.model.StaffRole.Permission;
import com.zeromones.model.StaffUser;
import org.springframework.stereotype.Service;

/**
 * Service for checking user permissions
 */
@Service
public class PermissionService {

    /**
     * Check if a user has a specific permission
     */
    public boolean hasPermission(StaffUser user, Permission permission) {
        if (user == null || !user.isActive()) {
            return false;
        }

        StaffRole role = StaffRole.fromString(user.getRole());
        return role.hasPermission(permission);
    }

    /**
     * Check if a user can access a specific municipality's data
     */
    public boolean canAccessMunicipality(StaffUser user, String municipality) {
        if (user == null || !user.isActive()) {
            return false;
        }

        StaffRole role = StaffRole.fromString(user.getRole());

        // ADMIN can access all municipalities
        if (role == StaffRole.ADMIN) {
            return true;
        }

        // MANAGER and OPERATOR can only access their own municipality
        return user.getMunicipality() != null &&
               user.getMunicipality().equalsIgnoreCase(municipality);
    }

    /**
     * Validate if user can view all bookings (cross-municipality)
     */
    public boolean canViewAllBookings(StaffUser user) {
        return hasPermission(user, Permission.VIEW_ALL_BOOKINGS);
    }

    /**
     * Validate if user can manage employees
     */
    public boolean canManageEmployees(StaffUser user) {
        return hasPermission(user, Permission.MANAGE_EMPLOYEES);
    }

    /**
     * Validate if user can create work lists
     */
    public boolean canManageWorkLists(StaffUser user) {
        return hasPermission(user, Permission.MANAGE_WORKLISTS);
    }

    /**
     * Validate if user can assign requests to work lists
     */
    public boolean canAssignRequests(StaffUser user) {
        return hasPermission(user, Permission.ASSIGN_REQUESTS);
    }

    /**
     * Get the municipality filter for a user
     * Returns null if user can see all municipalities (ADMIN)
     * Returns user's municipality otherwise
     */
    public String getMunicipalityFilter(StaffUser user) {
        if (user == null || !user.isActive()) {
            return null;
        }

        StaffRole role = StaffRole.fromString(user.getRole());

        // ADMIN can see all
        if (role == StaffRole.ADMIN) {
            return null;
        }

        // Others only see their municipality
        return user.getMunicipality();
    }
}
