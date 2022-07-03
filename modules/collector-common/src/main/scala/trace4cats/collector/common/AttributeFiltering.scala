package trace4cats.collector.common

import cats.syntax.semigroup._
import fs2.Pipe
import trace4cats.collector.common.config._
import trace4cats.filtering.AttributeFilter._
import trace4cats.filtering.PipeAttributeFilter
import trace4cats.model.CompletedSpan

object AttributeFiltering {
  def pipe[F[_]](config: FilteringConfig): Pipe[F, CompletedSpan, CompletedSpan] = {
    val filter = config match {
      case FilteringConfig(Some(ns), Some(vs), Some(nvs)) => Some(names(ns) |+| values(vs) |+| nameValues(nvs))
      case FilteringConfig(Some(ns), Some(vs), None) => Some(names(ns) |+| values(vs))
      case FilteringConfig(Some(ns), None, None) => Some(names(ns))
      case FilteringConfig(Some(ns), None, Some(nvs)) => Some(names(ns) |+| nameValues(nvs))
      case FilteringConfig(None, Some(vs), Some(nvs)) => Some(values(vs) |+| nameValues(nvs))
      case FilteringConfig(None, Some(vs), None) => Some(values(vs))
      case FilteringConfig(None, None, Some(nvs)) => Some(nameValues(nvs))
      case FilteringConfig(None, None, None) => None
    }

    filter.fold[Pipe[F, CompletedSpan, CompletedSpan]](identity)(f => PipeAttributeFilter(f))
  }
}
