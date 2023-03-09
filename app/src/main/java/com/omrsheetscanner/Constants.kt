package com.omrsheetscanner

import org.opencv.core.Scalar
import org.opencv.core.Size

object Constants {
    val FULL_Y_CAMERA = 620
    val FULL_X_CAMERA = 860
    val TOP_RIGHT_X = 50
    val LEFT_X = 570
    val BOTTOM_Y = 760
    val HIGHT_BOX = 100
    val WIDTH_BOX = 100

    val BLACK = Scalar(0.0, 0.0, 0.0)
    val BLUE = Scalar(0.0, 0.0, 255.0)
    val COUNTOUR_IDX = -1
    val THICKNESS_BOX = 2

    val GAUSSIAN_KERNEL_SIZE = Size(5.0, 5.0)
    val GAUSSIAN_SIGMA = 0.0

    val THRESHOLD_CANNY = 50.0

    val PERCENT_OF_PERIMETER = 0.04

    val MIN_RATIO = 0.5
    val MAX_RATIO = 1.1

    val MIN_AREA = 500.0
    val MAX_AREA = 1000.0

    val SQUARE_POINTS = 4
}