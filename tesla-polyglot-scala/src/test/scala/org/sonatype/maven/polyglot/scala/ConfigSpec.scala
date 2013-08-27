/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.scala

import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith
import org.sonatype.maven.polyglot.scala.model._
import org.codehaus.plexus.util.xml.Xpp3Dom

@RunWith(classOf[JUnitRunner])
class ConfigSpec extends Specification {

  "The config" should {
    "should convert from an xml doc to a config" in {
      val xml = new Xpp3Dom("configuration")
      val child1 = new Xpp3Dom("key1")
      xml.addChild(child1)
      val child2 = new Xpp3Dom("key2")
      child2.setValue("value2")
      child1.addChild(child2)
      val child3 = new Xpp3Dom("key3")
      child1.addChild(child3)

      val c = new ConvertibleMavenConfig(xml)
      val es = c.asScala.elements

      es.size must_== 1
      es(0)._1 must_== "key1"
      val e = es(0)._2.get.asInstanceOf[Config].elements
      e.size must_== 2
      e(0)._1 must_== "key2"
      e(0)._2.get.asInstanceOf[String] must_== "value2"
      e(1)._1 must_== "key3"
      e(1)._2 must beNone
    }
    "should convert from a config to an xml doc" in {
      val config = new Config(
        Seq(
          "key1" -> Some(new Config(
            Seq(
              "key2" -> Some("value2"),
              "key3" -> None
            )
          ))
        )
      )

      val c = new ConvertibleScalaConfig(config)
      val xml = c.asJava

      xml.getName must_== "configuration"
      xml.getChildCount must_== 1
      val child1 = xml.getChild(0)
      child1.getName must_== "key1"
      child1.getChildCount must_== 2
      val child2 = child1.getChild(0)
      child2.getName must_== "key2"
      child2.getValue must_== "value2"
      val child3 = child1.getChild(1)
      child3.getName must_== "key3"
      child3.getValue must beNull
    }
    "should permit elements to be assigned via dynamic apply" in {
      val config = Config.apply(key1 = Config.apply(key2 = "value2", key3 = None))

      val es = config.elements

      es.size must_== 1
      es(0)._1 must_== "key1"
      val e = es(0)._2.get.asInstanceOf[Config].elements
      e.size must_== 2
      e(0)._1 must_== "key2"
      e(0)._2.get.asInstanceOf[String] must_== "value2"
      e(1)._1 must_== "key3"
      e(1)._2 must beNone
    }
  }
}
