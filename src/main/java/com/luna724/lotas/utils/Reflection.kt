package com.luna724.lotas.utils

class Reflection {
    companion object {
        fun readAccessibleField(target: Any, names: Array<String>, tryReflection: Boolean = false, requires: Boolean = false, default: Any? = null): Any? {
            val clazz = target.javaClass
            for (name in names) {
                try {
                    val field = clazz.getField(name)
                    return field.get(target)
                } catch (_: NoSuchFieldException) {
                    continue
                }
            }
            if (tryReflection) {
                return readField(target, names, requires = requires)
            }
            if (requires) {
                throw NullPointerException(
                    "Field not found: ${target.javaClass.name} with names ${names.joinToString(", ")}"
                )
            }
            return default
        }

        /**
         * 遅い
         * 基本的には readAccessibleFieldを利用する
         */
        fun readField(target: Any, names: Array<String>, requires: Boolean = false): Any? {
            for (name in names) {
                var type: Class<*>? = target.javaClass
                while (type != null) {
                    try {
                        val field = type.getDeclaredField(name)
                        field.isAccessible = true
                        return field.get(target)
                    } catch (_: NoSuchFieldException) {
                        type = type.superclass
                    } catch (_: SecurityException) {
                        break
                    }
                }
            }
            if (requires) {
                throw NullPointerException(
                    "Field not found: ${target.javaClass.name} with names ${names.joinToString(", ")}"
                )
            }
            return null
        }
    }
}