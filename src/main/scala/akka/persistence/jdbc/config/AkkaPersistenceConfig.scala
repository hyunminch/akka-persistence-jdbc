/*
 * Copyright 2016 Dennis Vriend
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package akka.persistence.jdbc.config

import akka.persistence.jdbc.util.ConfigOps._
import com.typesafe.config.Config

import scala.concurrent.duration.{ FiniteDuration, _ }

class SlickConfiguration(config: Config) {
  private val cfg = config.asConfig("slick")
  val slickDriver: String = cfg.as[String]("driver", "slick.driver.PostgresDriver$")
  val jndiName: Option[String] = cfg.as[String]("jndiName").trim
  val jndiDbName: Option[String] = cfg.as[String]("jndiDbName")
  override def toString: String = s"SlickConfiguration($slickDriver,$jndiName,$jndiDbName)"
}

class JournalTableColumnNames(config: Config) {
  private val cfg = config.asConfig("tables.journal.columnNames")
  val persistenceId: String = cfg.as[String]("persistenceId", "persistence_id")
  val sequenceNumber: String = cfg.as[String]("sequenceNumber", "sequence_number")
  val created: String = cfg.as[String]("created", "created")
  val tags: String = cfg.as[String]("tags", "tags")
  val message: String = cfg.as[String]("message", "message")
  override def toString: String = s"JournalTableColumnNames($persistenceId,$sequenceNumber,$created,$tags,$message)"
}

class JournalTableConfiguration(config: Config) {
  private val cfg = config.asConfig("tables.journal")
  val tableName: String = cfg.as[String]("tableName", "journal")
  val schemaName: Option[String] = cfg.as[String]("schemaName").trim
  val columnNames: JournalTableColumnNames = new JournalTableColumnNames(config)
  override def toString: String = s"JournalTableConfiguration($tableName,$schemaName,$columnNames)"
}

class DeletedToTableColumnNames(config: Config) {
  private val cfg = config.asConfig("tables.deletedTo.columnNames")
  val persistenceId: String = cfg.as[String]("persistenceId", "persistence_id")
  val deletedTo: String = cfg.as[String]("deletedTo", "deleted_to")
  override def toString: String = s"DeletedToTableColumnNames($persistenceId,$deletedTo)"
}

class DeletedToTableConfiguration(config: Config) {
  private val cfg = config.asConfig("tables.deletedTo")
  val tableName: String = cfg.as[String]("tableName", "deleted_to")
  val schemaName: Option[String] = cfg.as[String]("schemaName").trim
  val columnNames: DeletedToTableColumnNames = new DeletedToTableColumnNames(config)
  override def toString: String = s"DeletedToTableConfiguration($tableName,$schemaName,$columnNames)"
}

class SnapshotTableColumnNames(config: Config) {
  private val cfg = config.asConfig("tables.snapshot.columnNames")
  val persistenceId: String = cfg.as[String]("persistenceId", "persistence_id")
  val sequenceNumber: String = cfg.as[String]("sequenceNumber", "sequence_number")
  val created: String = cfg.as[String]("created", "created")
  val snapshot: String = cfg.as[String]("snapshot", "snapshot")
  override def toString: String = s"SnapshotTableColumnNames($persistenceId,$sequenceNumber,$created,$snapshot)"
}

class SnapshotTableConfiguration(config: Config) {
  private val cfg = config.asConfig("tables.snapshot")
  val tableName: String = cfg.as[String]("tableName", "snapshot")
  val schemaName: Option[String] = cfg.as[String]("schemaName").trim
  val columnNames: SnapshotTableColumnNames = new SnapshotTableColumnNames(config)
  override def toString: String = s"SnapshotTableConfiguration($tableName,$schemaName,$columnNames)"
}

class JournalPluginConfig(config: Config) {
  val tagSeparator: String = config.as[String]("tagSeparator", ",")
  val serialization: Boolean = config.asBoolean("serialization", true)
  val dao: String = config.as[String]("dao", "akka.persistence.jdbc.dao.bytea.ByteArrayJournalDao")
  override def toString: String = s"JournalPluginConfig($tagSeparator,$serialization,$dao)"
}

class ReadJournalPluginConfig(config: Config) {
  val tagSeparator: String = config.as[String]("tagSeparator", ",")
  val serialization: Boolean = config.asBoolean("serialization", true)
  val dao: String = config.as[String]("dao", "akka.persistence.jdbc.dao.bytea.ByteArrayReadJournalDao")
  override def toString: String = s"ReadJournalPluginConfig($tagSeparator,$serialization,$dao)"
}

class SnapshotPluginConfig(config: Config) {
  val serialization: Boolean = config.asBoolean("serialization", true)
  val dao: String = config.as[String]("dao", "akka.persistence.jdbc.dao.bytea.ByteArraySnapshotDao")
  override def toString: String = s"SnapshotPluginConfig($serialization,$dao)"
}

// aggregations

class JournalConfig(config: Config) {
  val slickConfiguration = new SlickConfiguration(config)
  val journalTableConfiguration = new JournalTableConfiguration(config)
  val deletedToTableConfiguration = new DeletedToTableConfiguration(config)
  val pluginConfig = new JournalPluginConfig(config)
  override def toString: String = s"JournalConfig($slickConfiguration,$journalTableConfiguration,$deletedToTableConfiguration,$pluginConfig)"
}

class SnapshotConfig(config: Config) {
  val slickConfiguration = new SlickConfiguration(config)
  val snapshotTableConfiguration = new SnapshotTableConfiguration(config)
  val pluginConfig = new SnapshotPluginConfig(config)
  override def toString: String = s"SnapshotConfig($slickConfiguration,$snapshotTableConfiguration,$pluginConfig)"
}

class ReadJournalConfig(config: Config) {
  val slickConfiguration = new SlickConfiguration(config)
  val journalTableConfiguration = new JournalTableConfiguration(config)
  val pluginConfig = new ReadJournalPluginConfig(config)
  val refreshInterval: FiniteDuration = config.asFiniteDuration("refresh-interval", 1.second)
  val maxBufferSize: Int = config.as[String]("max-buffer-size", "500").toInt
  override def toString: String = s"ReadJournalConfig($slickConfiguration,$journalTableConfiguration,$pluginConfig,$refreshInterval,$maxBufferSize)"
}