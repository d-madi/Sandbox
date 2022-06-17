package com.madidulatov.spanexmpl

import android.content.Context
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat

private typealias Digit = MaskedDateEditText.TemplateString.Symbol.Digit
private typealias Placeholder = MaskedDateEditText.TemplateString.Symbol.Placeholder

class MaskedDateEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr) {

    private val template: TemplateString = TemplateString(
        arrayOf(
            Digit('Д'), Digit('Д'),
            Placeholder('.'),
            Digit('М'), Digit('М'),
            Placeholder('.'),
            Digit('Г'), Digit('Г'), Digit('Г'), Digit('Г')
        )
    )

    private val watcher = Watcher()
    private val red = ContextCompat.getColor(context, android.R.color.holo_red_dark)

    init {
        setText(template.getStyled(red))
        addTextChangedListener(watcher)
    }

    fun setValue(value: String?) {
        value?.let {
            template.setText(it)
            updateText()
        }
    }

    fun isEmpty(): Boolean = template.getText().isEmpty()

    private fun updateText() {
        if (watcher.tryLock()) {
            try {
                if (template.getText().isNotEmpty()) {
                    setText(template.toString())
                    setSelection(template.getLastPosition())
                }
            } finally {
                watcher.unlock()
            }
        } else {
//			 should never happen, affects UX
            postDelayed({ updateText() }, 10L)
        }
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        when {
            text.isNullOrEmpty() -> setSelection(0) //don't remove this inspection cause onSelectionChanged called before template initialized
            selEnd > selStart && selEnd > template.getLastPosition() -> setSelection(
                selStart,
                template.getLastPosition()
            )
            selStart > template.getLastPosition() || selEnd > template.getLastPosition() -> setSelection(
                template.getLastPosition()
            )
            else -> super.onSelectionChanged(selStart, selEnd)
        }
    }

    private inner class Watcher : TextWatcher {
        private var locked = false

        override fun afterTextChanged(s: Editable) {
            if (tryLock()) {
                try {
                    s.filters = arrayOf()
                    s.getSpans(0, s.length, ForegroundColorSpan::class.java)
                        .forEach { s.removeSpan(it) }
                    s.replace(0, s.length, template.getStyled(red))
                    setSelection(template.position)
                } finally {
                    unlock()
                }
            }
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (tryLock()) {
                try {
                    template.apply(s, start, before, count)
                } finally {
                    unlock()
                }
            }
        }

        fun tryLock() = if (locked) false else {
            locked = true
            true
        }

        fun unlock() {
            locked = false
        }
    }

    class TemplateString(private val template: Array<Symbol>) {
        var position: Int = 0
            private set

        fun getLastPosition(): Int {
            var field = 0
            while (field < template.size && template[field].value != null) field++
            return field
        }

        fun getStyled(foregroundColor: Int): CharSequence {
            val str = SpannableStringBuilder()
            template.forEachIndexed { index, symbol ->
                val ch = symbol.value
                if (ch != null) str.append(ch) else {
                    str.append(symbol.mask)
                    str.setSpan(ForegroundColorSpan(foregroundColor), index, index + 1, 0)
                }
            }
            return str
        }

        fun setText(str: String) {
            var pos = 0
            var i = 0
            while (pos < template.size && i < str.length) {
                val ch = str[i]
                if (template[pos] is Placeholder) pos++
                else {
                    if (template[pos].accepts(ch))
                        template[pos++].value = ch
                    i++
                }
            }

            position = pos
        }

        override fun toString(): String {
            val str = StringBuilder(11)
            template.forEach { symbol -> symbol.value?.let { str.append(it) } ?: return@forEach }
            return str.toString()
        }

        fun apply(
            value: CharSequence,
            start: Int,
            before: Int,
            count: Int
        ) {
            var pos = start
            var delCount = before
            if (before >= 1 && count == 0) {
                while (pos > 0 && template[pos] is Placeholder) {
                    if (before > 1) {
                        pos++
                        delCount--
                    } else
                        pos--
                }
                deleteSegment(pos, delCount)
                position = pos
            } else {
                while (pos < template.size && template[pos] is Placeholder) {
                    pos++
                }
                if (pos == template.size) return

                if (before < 1) {
                    val len = count + start
                    var i = start
                    while (pos < template.size && i < len) {
                        val ch = value[i]
                        if (template[pos] is Placeholder) pos++
                        if (template[pos].accepts(ch)) {
                            insert(pos, ch)
                            pos++
                            i++
                        } else i++
                    }
                } else
                    insert(pos, before, value, count)
            }
        }

        private fun deleteChar(pos: Int) {
            var i = pos + 1
            var currPos = pos

            while (i != template.size && template[i].value != null && template[currPos].value != null) {
                if (template[currPos] is Placeholder) currPos++
                if (template[i] is Placeholder) i++
                template[currPos].value =
                    template[i].value?.let { if (template[currPos].accepts(it)) it else null }
                i++
                currPos++
            }

            //fill free places with mask
            var j = currPos
            while (j < template.size) {
                if (template[j] is Placeholder) j++
                template[j].value = null
                j++
            }
        }

        private fun deleteSegment(start: Int, count: Int) {
            var i = start
            val len = if (start + count > template.size) template.size else start + count
            while (i < len) {
                if (template[i] is Placeholder) i++
                else {
                    deleteChar(start)
                    i++
                }
            }
        }

        private fun insert(position: Int, value: Char) {
            var prev: Char? = value
            var i = position
            while (i < template.size && prev != null) {
                val symbol = template[i]
                if (symbol !is Placeholder) {
                    if (symbol.accepts(prev)) {
                        val temp = symbol.value
                        symbol.value = prev
                        prev = temp
                        this.position = position + 1
                    } else return
                }
                i++
            }

        }

        private fun insert(start: Int, end: Int, values: CharSequence, count: Int) {
            var i = start
            var pos = start
            val len = start + count
            deleteSegment(start, end)
            while (i < len && pos < template.size) {
                if (template[pos] is Placeholder) pos++
                if (template[pos].accepts(values[i])) {
                    insert(pos, values[i])
                    pos++
                    i++
                } else i++
            }
        }

        sealed class Symbol(val mask: Char) {

            class Placeholder(char: Char) : Symbol(char) {
                override var value: Char? = char
                    set(value) {
                        throw IllegalArgumentException("Can not set Placeholder value")
                    }

                override fun accepts(char: Char) = value == char
            }

            class Digit(mask: Char) : Symbol(mask) {
                override var value: Char? = null
                    set(value) {
                        if (value != null && !accepts(value)) throw IllegalArgumentException("Can not set $value to Digit")
                        field = value
                    }

                override fun accepts(char: Char) = char.isDigit()
            }

            abstract var value: Char?
            abstract fun accepts(char: Char): Boolean
        }

        fun getText(): String {
            var i = 0
            val chars = CharArray(template.size)
            template.filter { it !is Placeholder }
                .takeWhile { it.value != null }
                .forEach { it.value?.let { chars[i++] = it } }

            return String(chars, 0, i)
        }
    }
}