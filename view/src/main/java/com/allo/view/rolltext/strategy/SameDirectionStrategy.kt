package com.allo.view.rolltext.strategy

class SameDirectionStrategy(
        private val direction: Direction,
        private val otherStrategy: CharOrderStrategy = NormalAnimationStrategy()
) : SimpleCharOrderStrategy() {

    override fun findCharOrder(
            sourceText: CharSequence,
            targetText: CharSequence,
            index: Int,
            charPool: CharPool): Pair<List<Char>, Direction> {

        return otherStrategy.findCharOrder(sourceText, targetText, index, charPool).first to direction
    }
}