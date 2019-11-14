package bbox.com.todoapp_kotlin_mvp.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Todo (

    @SerializedName("id")
    @Expose
    var id: Int,

    @SerializedName("name")
    @Expose
    var name: String,

    @SerializedName("status")
    @Expose
    var status: String,

    @SerializedName("description")
    @Expose
    var description: String,

    @SerializedName("expiry_date")
    @Expose
    var expiry_date: String

):Parcelable

    {

        override fun toString(): String {
            return "BlogPost(id=$id, name=$name, Desc=$description)"
        }


    //region .......parcalable

        constructor(parcelIn: Parcel):this(
                parcelIn.readInt(),
                parcelIn.readString(),
                parcelIn.readString(),
                parcelIn.readString(),
                parcelIn.readString()
        )

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeInt(id)
            dest.writeString(name)
            dest.writeString(status)
            dest.writeString(description)
            dest.writeString(expiry_date)
        }

        override fun describeContents(): Int {
            return 0
        }


        companion object CREATOR : Parcelable.Creator<Todo> {
            override fun createFromParcel(parcel: Parcel): Todo {
                return Todo(parcel)
            }

            override fun newArray(size: Int): Array<Todo?> {
                return arrayOfNulls(size)
            }
        }



    //endregion
    }
