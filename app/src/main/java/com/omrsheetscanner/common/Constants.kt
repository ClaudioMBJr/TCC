package com.omrsheetscanner.common

import org.opencv.core.Scalar

object Constants {
    val GREEN = Scalar(0.0, 255.0, 0.0)
    val BLUE = Scalar(0.0, 0.0, 255.0)
    val RED = Scalar(255.0, 0.0, 0.0)
    val YELLOW = Scalar(255.0, 255.0, 0.0)
    val COUNTOUR_IDX = -1
    val THICKNESS_BOX = 2

    val PERCENT_OF_PERIMETER = 0.02

    val MIN_RATIO = 1.8
    val MAX_RATIO = 2.3

    val SQUARE_POINTS = 4

    val FILE_NAME = "my_image"
}