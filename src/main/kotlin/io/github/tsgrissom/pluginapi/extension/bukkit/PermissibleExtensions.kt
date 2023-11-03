package io.github.tsgrissom.pluginapi.extension.bukkit

import org.bukkit.permissions.Permissible
import org.bukkit.permissions.Permission

fun Permissible.hasAnyPermissions(vararg permissions: String) =
    permissions.any { this.hasPermission(it) }

fun Permissible.hasAnyPermissions(vararg permissions: Permission) =
    permissions.any { this.hasPermission(it) }

fun Permissible.hasAllPermissions(vararg permissions: String) =
    permissions.all { this.hasPermission(it) }

fun Permissible.hasAllPermissions(vararg permissions: Permission) =
    permissions.all { this.hasPermission(it) }

/**
 * Checks if the CommandSender is missing the requisite permission.
 *
 * @param permission The permission to check if the user is missing.
 * @return Whether the user lacks the requisite permission.
 */
fun Permissible.lacksPermission(permission: String) =
    !this.hasPermission(permission)

/**
 * Checks if the CommandSender is missing the requisite permission.
 *
 * @param permission The permission to check if the user is missing.
 * @return Whether the user lacks the requisite permission.
 */
fun Permissible.lacksPermission(permission: Permission) =
    !this.hasPermission(permission)