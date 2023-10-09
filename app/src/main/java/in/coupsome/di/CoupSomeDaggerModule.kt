package `in`.coupsome.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UsersReference

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AllCouponsReference

@Module
@InstallIn(SingletonComponent::class)
object CoupSomeDaggerModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context

    @Singleton
    @Provides
    fun provideDatabaseInstance(): FirebaseDatabase =
        FirebaseDatabase.getInstance()

    @Singleton
    @Provides
    @UsersReference
    fun provideUsersRef(firebaseDatabase: FirebaseDatabase): DatabaseReference =
        firebaseDatabase.getReference("Users")

    @Singleton
    @Provides
    @AllCouponsReference
    fun provideAllCouponsRef(firebaseDatabase: FirebaseDatabase): DatabaseReference =
        firebaseDatabase.getReference("all_coupons")

    @Singleton
    @Provides
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun provideCurrentUser(firebaseAuth: FirebaseAuth): FirebaseUser? = firebaseAuth.currentUser
}