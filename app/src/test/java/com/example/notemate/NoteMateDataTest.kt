package com.example.notemate

import com.example.notemate.data.model.NoteMateData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NoteMateDataTest {

    @Test
    fun testCourseCatalogNotEmpty() {
        val courses = NoteMateData.courses
        assertTrue(courses.isNotEmpty())
        assertEquals(19, courses.size)

        val btech = courses.find { it.id == "btech" }
        assertNotNull(btech)
        assertEquals("B.Tech", btech?.title)
        assertTrue(btech?.tags?.contains("CSE") == true)
    }

    @Test
    fun testPlansData() {
        val plans = NoteMateData.plans
        assertEquals(3, plans.size)

        val proPlan = plans.find { it.id == "pro" }
        assertNotNull(proPlan)
        assertTrue(proPlan?.isRecommended == true)
    }

    @Test
    fun testFaqsAndTestimonials() {
        assertTrue(NoteMateData.faqs.isNotEmpty())
        assertTrue(NoteMateData.testimonials.isNotEmpty())
    }
}
