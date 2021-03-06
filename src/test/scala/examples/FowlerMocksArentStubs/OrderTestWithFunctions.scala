// Copyright (c) 2011 Paul Butcher
// 
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
// 
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

package com.borachio.examples

import org.scalatest.WordSpec
import com.borachio.scalatest.MockFactory

// This is a reworked version of the example from Martin Fowler's article
// Mocks Aren't Stubs http://martinfowler.com/articles/mocksArentStubs.html
class OrderTestWithFunctions extends WordSpec with MockFactory {
  
  val hasInventoryMock = mockFunction[String, Int, Boolean]
  val removeMock = mockFunction[String, Int, Unit]

  val mockWarehouse = new Warehouse {
    def hasInventory(product: String, quantity: Int) = hasInventoryMock(product, quantity)
    def remove(product: String, quantity: Int) = removeMock(product, quantity)
  }
  
  "An order" when {
    "in stock" should {
      "remove inventory" in {
        inSequence {
          hasInventoryMock expects ("Talisker", 50) returning true once;
          removeMock expects ("Talisker", 50) once
        }
        
        val order = new Order("Talisker", 50)
        order.fill(mockWarehouse)
        
        assert(order.isFilled)
      }
    }
    
    "out of stock" should {
      "remove nothing" in {
        hasInventoryMock returns false once
        
        val order = new Order("Talisker", 50)
        order.fill(mockWarehouse)
        
        assert(!order.isFilled)
      }
    }
  }
}
