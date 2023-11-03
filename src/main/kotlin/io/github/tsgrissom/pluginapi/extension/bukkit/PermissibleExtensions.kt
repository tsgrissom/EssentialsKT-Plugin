package io.github.tsgrissom.pluginapi.extension.bukkit

import org.bukkit.permissions.Permissible
import org.bukkit.permissions.Permission

/**
 * Checks if the Permissible has any one permission provided to the permissions parameter.
 * @param permissions Any amount of Permissions to check for.
 * @return Whether the Permissible has any of the given permissions.
 */
fun Permissible.hasAnyPermissions(vararg permissions: String) =
    permissions.any { this.hasPermission(it) }

/**
 * Checks if the Permissible has any one permission provided to the permissions parameter.
 * @param permissions Any amount of permissions as Strings to check for.
 * @return Whether the Permissible has any of the given permissions.
 */
fun Permissible.hasAnyPermissions(vararg permissions: Permission) =
    permissions.any { this.hasPermission(it) }

/**
 * Checks if the Permissible has every single permission provided to the permissions parameter.
 * @param permissions Any amount of permissions as Strings to check for.
 * @return Whether the Permissible has all the given permissions.
 */
fun Permissible.hasAllPermissions(vararg permissions: String) =
    permissions.all { this.hasPermission(it) }

/**
 * Checks if the Permissible has every single permission provided to the permissions parameter.
 * @param permissions Any amount of Permissions to check for.
 * @return Whether the Permissible has all the given permissions.
 */
fun Permissible.hasAllPermissions(vararg permissions: Permission) =
    permissions.all { this.hasPermission(it) }

/**
 * Checks if the CommandSender is missing the requisite permission.
 * @param permission The permission to check if the user is missing.
 * @return Whether the user lacks the requisite permission.
 */
fun Permissible.lacksPermission(permission: String) =
    !this.hasPermission(permission)

/**
 * Checks if the CommandSender is missing the requisite permission.
 * @param permission The permission to check if the user is missing.
 * @return Whether the user lacks the requisite permission.
 */
fun Permissible.lacksPermission(permission: Permission) =
    !this.hasPermission(permission)