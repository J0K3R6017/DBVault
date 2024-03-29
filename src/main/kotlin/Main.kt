package com.j0k3r6017

import com.j0k3r6017.presentation.DBVault

fun main(args: Array<String>) {
    if(args.isNotEmpty()) {
        val dbVault = DBVault(args[0])
        when(args[1]) {
            "make:migration" -> {
                println("Migrando base de datos...")
                dbVault.makeMigration(args[2])
            }
            "migrate" -> {
                dbVault.migrate()
            }
            "migrate:rollback" -> {
                val step = args.getOrNull(2)?.toInt()
                dbVault.rollback(step)
            }
            "migrate:fresh" -> {
                dbVault.rollback(-1)
                dbVault.migrate()
            }
            else -> {
                println("Comando no válido")
            }
        }
    } else {
        println("Faltan argumentos")
    }
}