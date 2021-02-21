package world.shanya.serialport

class SerialPort private constructor() {
    companion object {
        private var instance: SerialPort? = null
            get() {
                if (field == null) {
                    field = SerialPort()
                }
                return field
            }

        @Synchronized
        fun get(): SerialPort {
            return instance!!
        }
    }
}