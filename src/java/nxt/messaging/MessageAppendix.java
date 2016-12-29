/*
 * Copyright © 2013-2016 The Nxt Core Developers.
 * Copyright © 2016 Jelurida IP B.V.
 *
 * See the LICENSE.txt file at the top-level directory of this distribution
 * for licensing information.
 *
 * Unless otherwise agreed in a custom licensing agreement with Jelurida B.V.,
 * no part of the Nxt software, including this file, may be copied, modified,
 * propagated, or distributed except according to the terms contained in the
 * LICENSE.txt file.
 *
 * Removal or modification of this copyright notice is prohibited.
 *
 */

package nxt.messaging;

import nxt.Constants;
import nxt.NxtException;
import nxt.account.Account;
import nxt.blockchain.Appendix;
import nxt.blockchain.Chain;
import nxt.blockchain.ChildChain;
import nxt.blockchain.Fee;
import nxt.blockchain.Transaction;
import nxt.blockchain.TransactionImpl;
import nxt.util.Convert;
import org.json.simple.JSONObject;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class MessageAppendix extends Appendix.AbstractAppendix {

    public static final int appendixType = 1;
    public static final String appendixName = "Message";

    public static final AppendixParser appendixParser = new AppendixParser() {
        @Override
        public AbstractAppendix parse(ByteBuffer buffer) throws NxtException.NotValidException {
            return new MessageAppendix(buffer);
        }

        @Override
        public AbstractAppendix parse(JSONObject attachmentData) {
            if (!Appendix.hasAppendix(appendixName, attachmentData)) {
                return null;
            }
            return new MessageAppendix(attachmentData);
        }
    };

    private static final Fee MESSAGE_FEE = new Fee.SizeBasedFee(0, Constants.ONE_NXT, 32) {
        @Override
        public int getSize(TransactionImpl transaction, Appendix appendage) {
            return ((MessageAppendix)appendage).getMessage().length;
        }
    };

    private final byte[] message;
    private final boolean isText;

    private MessageAppendix(ByteBuffer buffer) throws NxtException.NotValidException {
        super(buffer);
        int messageLength = buffer.getInt();
        this.isText = messageLength < 0; // ugly hack
        if (messageLength < 0) {
            messageLength &= Integer.MAX_VALUE;
        }
        if (messageLength > 1000) {
            throw new NxtException.NotValidException("Invalid arbitrary message length: " + messageLength);
        }
        this.message = new byte[messageLength];
        buffer.get(this.message);
        if (isText && !Arrays.equals(message, Convert.toBytes(Convert.toString(message)))) {
            throw new NxtException.NotValidException("Message is not UTF-8 text");
        }
    }

    private MessageAppendix(JSONObject attachmentData) {
        super(attachmentData);
        String messageString = (String)attachmentData.get("message");
        this.isText = Boolean.TRUE.equals(attachmentData.get("messageIsText"));
        this.message = isText ? Convert.toBytes(messageString) : Convert.parseHexString(messageString);
    }

    public MessageAppendix(byte[] message) {
        this(message, false);
    }

    public MessageAppendix(String string) {
        this(Convert.toBytes(string), true);
    }

    public MessageAppendix(String string, boolean isText) {
        this(isText ? Convert.toBytes(string) : Convert.parseHexString(string), isText);
    }

    public MessageAppendix(byte[] message, boolean isText) {
        this.message = message;
        this.isText = isText;
    }

    @Override
    public int getAppendixType() {
        return appendixType;
    }

    @Override
    public String getAppendixName() {
        return appendixName;
    }

    @Override
    protected int getMySize() {
        return 4 + message.length;
    }

    @Override
    protected void putMyBytes(ByteBuffer buffer) {
        buffer.putInt(isText ? (message.length | Integer.MIN_VALUE) : message.length);
        buffer.put(message);
    }

    @Override
    protected void putMyJSON(JSONObject json) {
        json.put("message", Convert.toString(message, isText));
        json.put("messageIsText", isText);
    }

    @Override
    public Fee getBaselineFee(Transaction transaction) {
        return MESSAGE_FEE;
    }

    @Override
    public void validate(Transaction transaction) throws NxtException.ValidationException {
        if (message.length > Constants.MAX_ARBITRARY_MESSAGE_LENGTH) {
            throw new NxtException.NotValidException("Invalid arbitrary message length: " + message.length);
        }
    }

    @Override
    public void apply(Transaction transaction, Account senderAccount, Account recipientAccount) {}

    public byte[] getMessage() {
        return message;
    }

    public boolean isText() {
        return isText;
    }

    @Override
    public boolean isPhasable() {
        return false;
    }

    @Override
    public boolean isAllowed(Chain chain) {
        return chain instanceof ChildChain;
    }

}
