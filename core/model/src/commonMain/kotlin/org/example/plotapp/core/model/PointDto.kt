package org.example.plotapp.core.model

import kotlinx.serialization.Serializable

/**
 * Represents a point with x and y coordinates.
 */
@Serializable
data class PointDto(
    val x: Double,
    val y: Double,
)