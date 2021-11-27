package com.example.stairtree

import org.junit.Test
import org.junit.Assert.*


class upAndDownJudgmentTest {
    @Test
    fun getSlope() {
        val judge = upAndDownJudgment(2)
        judge.push(0.2)
        judge.push(0.4)
        assertEquals(1.0, judge.sloop(), 0.1)
        judge.push(0.8)
        assertEquals(2.0,judge.sloop(),0.1)
        val judge2 = upAndDownJudgment(3)
        judge2.push(0.2)
        judge2.push(0.4)
        judge2.push(0.6)
        assertEquals(1.0, judge2.sloop(), 0.1)
        judge2.push(1.0)
        assertEquals(1.5,judge2.sloop(),0.1)
    }
    @Test
    fun canGetSlope(){
        val judge = upAndDownJudgment(2)
        judge.push(0.2)
        assert(!judge.possibleToJudge())
        judge.push(0.4)
        assert(judge.possibleToJudge())
        judge.push(0.8)
        assert(judge.possibleToJudge())

    }
}