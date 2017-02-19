package io.pivotal.trilogy.i18n

import org.springframework.context.i18n.LocaleContextHolder
import java.text.MessageFormat
import java.util.ResourceBundle

object MessageCreator {
    fun getI18nMessage(messagePath: String, messageArguments: List<Any> = emptyList()): String = MessageFormat(fetchRawI18nMessage(messagePath)).format(messageArguments.toTypedArray())
    private fun fetchRawI18nMessage(name: String): String = ResourceBundle.getBundle("messages", LocaleContextHolder.getLocale()).getString(name)
}