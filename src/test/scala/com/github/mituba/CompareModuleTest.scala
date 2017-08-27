package com.github.mituba

/**
  * Created by mituba on 2017/08/27.
  */


import java.io.{BufferedReader, File, FileReader}

import com.github.mituba.entity.{ClassInformation, CompareResult, SearchResult}
import com.github.mituba.module.CompareModule
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class CompareModuleTest extends FunSuite{
  test("sameInputCompareResult is same") {
    def compareModule = new CompareModule
    val a = new ClassInformation("a", "test.jar", "2-gram", "25 52, 22 22")
    val b = new ClassInformation("b", "test.jar", "2-gram", "25 52, 22 22")

    // same input
    val compareResult: CompareResult = compareModule.runCompare(List(new SearchResult(a, b, "0.0")))(0)
    assert(compareResult.class1 == a)
    assert(compareResult.class2 == b)
    assert(!(compareResult.class1 == b))
    assert(!(compareResult.class2 == a))
    assert(compareResult.sim == "1.0")
  }

  test("differentInputCompareResult is different") {
    def compareModule = new CompareModule
    val a = new ClassInformation("a", "test.jar", "2-gram", "25 52, 22 22")
    val b = new ClassInformation("b", "test.jar", "2-gram", "22 22")

    // different input
    val compareResult: CompareResult = compareModule.runCompare(List(new SearchResult(a, b, "0.0")))(0)
    assert(compareResult.class1 == a)
    assert(compareResult.class2 == b)
    assert(!(compareResult.class1 == b))
    assert(!(compareResult.class2 == a))
    assert(compareResult.sim != "1.0")
  }

  test("createFile successful"){
    def compareModule = new CompareModule
    val a = new ClassInformation("a", "test.jar", "2-gram", "25 52, 22 22")

    val aFile: File = compareModule.createFile(a, "class1.csv")
    assert(aFile.exists())
    val br = new BufferedReader(new FileReader(aFile)).readLine().split(",", 4)
    assert(a.filename == br(0))
    assert(a.place == br(1))
    assert(a.birthmark == br(2))
    assert(a.data == br(3))
  }
}
