package Helper;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Parcelable;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by rujoota on 17-11-2015.
 */
public class NFCUtils
{
    static NfcAdapter nfcAdapter;
    Tag tag;
    public static Boolean checkNFCAvailable(Context context,NfcAdapter nfcAdapter)
    {
        try
        {
            if (nfcAdapter != null && nfcAdapter.isEnabled())
            {
                return true;
            }
            else
                return false;
        }
        catch(Exception ex)
        {
            return false;
        }
    }
    public static String formatTag(Tag tag,NdefMessage msg)
    {
        try
        {
            NdefFormatable ndefFormatable=NdefFormatable.get(tag);
            if(ndefFormatable==null)
                return "Tag is not ndef formatable";
            ndefFormatable.connect();
            ndefFormatable.format(msg);
            ndefFormatable.close();
            return "success in formatTag";
        }
        catch(Exception ex)
        {
            return ex.toString();
        }
    }
    public static String writeCustom(Intent intent,String pkgName,String data)
    {
        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String mimeType = "application/vnd." + pkgName;
        String mimeData = data;
        NdefRecord record = NdefRecord.createMime(mimeType, mimeData.getBytes());
        NdefMessage message = new NdefMessage(new NdefRecord[] { record });
        Ndef ndef = Ndef.get(detectedTag);
        try {
            ndef.connect();
            ndef.writeNdefMessage(message);
            ndef.close();
            return "tag written!";
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return "IO connection error";
    }
    public static String writeNdefMsg(Tag tag,NdefMessage msg)
    {
        try
        {
            Ndef ndef=Ndef.get(tag);
            if(ndef==null)
                return formatTag(tag, msg);
            else {
                ndef.connect();
                if(!ndef.isWritable())
                {

                    ndef.close();
                    return "Its not writable";
                }
                ndef.writeNdefMessage(msg);
                ndef.close();
                return "Tag written!!!";
            }
        }
        catch(Exception ex)
        {
            return ex.toString();
        }
    }
    public static String readDataCustom(Intent intent)
    {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefRecord relayRecord = ((NdefMessage) rawMsgs[0]).getRecords()[0];
        return new String(relayRecord.getPayload());
    }
    private static NdefMessage createNdefMsg(String content)
    {
        NdefRecord record=createTextRecord(content);

        NdefMessage msg=new NdefMessage(new NdefRecord[]{record});
        return msg;
    }
    static Context context;
    private static NdefRecord createTextRecord(String content)
    {
        try
        {
            byte[] lang;
            lang = Locale.getDefault().getLanguage().getBytes("UTF-8");
            byte[] txt=content.getBytes();
            int langSize=lang.length;
            int txtSize=txt.length;
            ByteArrayOutputStream payload=new ByteArrayOutputStream(1+langSize+txtSize);
            payload.write((byte)(langSize & 0x1F));
            payload.write(lang,0,langSize);
            payload.write(txt,0,txtSize);
            return new NdefRecord(NdefRecord.TNF_WELL_KNOWN,NdefRecord.RTD_TEXT,new byte[0],payload.toByteArray());

        }
        catch (Exception ex)
        {
            Toast.makeText(context,ex.toString(),Toast.LENGTH_SHORT).show();
            return null;
        }
    }
    public static String read(Intent intent)
    {
        Parcelable[] parcelables=intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if(parcelables==null || parcelables.length==0)
        {
            return "no data found";
        }
        else
        {
            return readFromNFC((NdefMessage)parcelables[0]);
        }
    }
    public static String readFromNFC(NdefMessage msg)
    {
        NdefRecord[] ndefRecords=msg.getRecords();
        if(ndefRecords==null || ndefRecords.length==0)
            return "no records found";
        else {
            NdefRecord record=ndefRecords[0];
            String content=new String(record.getPayload());//readText(record);
            return "Read:"+content;
        }
    }
    private static String readText(NdefRecord record)
    {
        try
        {
            /*
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */
            byte[] payload = record.getPayload();

            // Get the Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;

            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            // e.g. "en"

            // Get the Text
            // String(data, offset, bytecount,encoding)
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }
        catch(Exception ex)
        {
            return null;
        }
    }
    public static String write(String strToWrite,Tag tag,Context c)
    {
        context=c;
        NdefMessage msg=createNdefMsg(strToWrite);
        return writeNdefMsg(tag,msg);
    }
}
