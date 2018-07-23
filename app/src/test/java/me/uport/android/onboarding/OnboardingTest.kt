/*
 * Copyright (c) 2018. uPort
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.uport.android.onboarding

import android.content.Context
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.whenever
import me.uport.android.fakes.InMemorySharedPrefs
import me.uport.android.onboarding.Onboarding.Companion.HAS_ACCEPTED_TOS
import me.uport.sdk.Uport
import me.uport.sdk.identity.Account
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class OnboardingTest {

    private val prefs = InMemorySharedPrefs()

    @Mock
    private lateinit var context: Context

    @Spy
    private val uportSDK = Uport

    @Before
    fun runBeforeEveryTest() {
        MockitoAnnotations.initMocks(this)
        whenever(context.getSharedPreferences(any(), any())).thenReturn(prefs)
    }

    @Test
    fun `onboarding state is BLANK when user hasn't accepted TOS`() {
        prefs.edit().remove(HAS_ACCEPTED_TOS).apply()
        assertEquals(Onboarding.State.BLANK, Onboarding(context).getState())
    }

    @Test
    fun `onboarding state is updated when user accepts TOS`() {
        val tested = Onboarding(context)

        tested.markTosAccepted()

        assertEquals(Onboarding.State.ACCEPTED_TOS, tested.getState())
    }

    @Test
    fun `onboarding is final after uport default account exists`() {
        whenever(uportSDK.defaultAccount).thenReturn(Account.blank)

        val tested = Onboarding(context, uportSDK).apply { markTosAccepted() }

        assertEquals(Onboarding.State.DEFAULT_ACCOUNT_EXISTS, tested.getState())

        assertTrue(tested.canShowDashboard())

    }
}