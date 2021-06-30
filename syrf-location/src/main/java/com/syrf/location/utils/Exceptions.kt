package com.syrf.location.utils

class NoConfigException : Exception("Config should be set before library use")

class MissingLocationException : Exception("The location permission is required for library usage")
