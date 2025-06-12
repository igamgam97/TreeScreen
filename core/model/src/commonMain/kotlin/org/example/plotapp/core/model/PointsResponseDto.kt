package org.example.plotapp.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model for the points API.
 */
@Serializable
data class PointsResponseDto(
    @SerialName("points")
    val pointDtoList: List<PointDto>,
)