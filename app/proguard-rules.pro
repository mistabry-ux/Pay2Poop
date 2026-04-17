-keepattributes *Annotation*
-keepclassmembers class * extends androidx.room.RoomDatabase {
    public static ** getInstance(...);
}
