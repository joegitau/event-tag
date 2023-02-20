package com.joegitau.utils

object Helpers {
  // wraps a value of type `T` in an Option
  implicit class OptionFns[T](value: T) {
    def toOpt: Option[T] = Option(value)
  }

  // caching
  class Cache[K, V](maxSize: Int, cache: Map[K, V] = Map.empty[K, V]) {
    def get(key: K): Option[V] = cache.get(key)

    def put(key: K, value: V): Cache[K, V] = {
      if (cache.size >= maxSize) {
        val (oldestKey, _) = cache.head
        new Cache[K, V](maxSize, cache - oldestKey + (key -> value))
      } else {
        new Cache[K, V](maxSize, cache + (key -> value))
      }
    }
  }

}
