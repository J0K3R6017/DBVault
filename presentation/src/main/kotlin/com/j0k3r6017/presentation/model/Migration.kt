package com.j0k3r6017.presentation.model

data class Migration(
    val up: Array<String>,
    val down: Array<String>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Migration

        if (!up.contentEquals(other.up)) return false
        if (!down.contentEquals(other.down)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = up.contentHashCode()
        result = 31 * result + down.contentHashCode()
        return result
    }
}