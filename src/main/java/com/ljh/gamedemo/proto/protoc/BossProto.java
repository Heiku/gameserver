// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: Boss.proto

package com.ljh.gamedemo.proto.protoc;

public final class BossProto {
  private BossProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface BossOrBuilder extends
      // @@protoc_insertion_point(interface_extends:Boss)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>int64 id = 1;</code>
     */
    long getId();

    /**
     * <code>string name = 2;</code>
     */
    java.lang.String getName();
    /**
     * <code>string name = 2;</code>
     */
    com.google.protobuf.ByteString
        getNameBytes();

    /**
     * <code>int64 hp = 3;</code>
     */
    long getHp();

    /**
     * <code>repeated .Spell spell = 4;</code>
     */
    java.util.List<com.ljh.gamedemo.proto.protoc.SpellProto.Spell> 
        getSpellList();
    /**
     * <code>repeated .Spell spell = 4;</code>
     */
    com.ljh.gamedemo.proto.protoc.SpellProto.Spell getSpell(int index);
    /**
     * <code>repeated .Spell spell = 4;</code>
     */
    int getSpellCount();
    /**
     * <code>repeated .Spell spell = 4;</code>
     */
    java.util.List<? extends com.ljh.gamedemo.proto.protoc.SpellProto.SpellOrBuilder> 
        getSpellOrBuilderList();
    /**
     * <code>repeated .Spell spell = 4;</code>
     */
    com.ljh.gamedemo.proto.protoc.SpellProto.SpellOrBuilder getSpellOrBuilder(
        int index);
  }
  /**
   * Protobuf type {@code Boss}
   */
  public  static final class Boss extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:Boss)
      BossOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use Boss.newBuilder() to construct.
    private Boss(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private Boss() {
      name_ = "";
      spell_ = java.util.Collections.emptyList();
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new Boss();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private Boss(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 8: {

              id_ = input.readInt64();
              break;
            }
            case 18: {
              java.lang.String s = input.readStringRequireUtf8();

              name_ = s;
              break;
            }
            case 24: {

              hp_ = input.readInt64();
              break;
            }
            case 34: {
              if (!((mutable_bitField0_ & 0x00000001) != 0)) {
                spell_ = new java.util.ArrayList<com.ljh.gamedemo.proto.protoc.SpellProto.Spell>();
                mutable_bitField0_ |= 0x00000001;
              }
              spell_.add(
                  input.readMessage(com.ljh.gamedemo.proto.protoc.SpellProto.Spell.parser(), extensionRegistry));
              break;
            }
            default: {
              if (!parseUnknownField(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        if (((mutable_bitField0_ & 0x00000001) != 0)) {
          spell_ = java.util.Collections.unmodifiableList(spell_);
        }
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.ljh.gamedemo.proto.protoc.BossProto.internal_static_Boss_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.ljh.gamedemo.proto.protoc.BossProto.internal_static_Boss_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.ljh.gamedemo.proto.protoc.BossProto.Boss.class, com.ljh.gamedemo.proto.protoc.BossProto.Boss.Builder.class);
    }

    public static final int ID_FIELD_NUMBER = 1;
    private long id_;
    /**
     * <code>int64 id = 1;</code>
     */
    public long getId() {
      return id_;
    }

    public static final int NAME_FIELD_NUMBER = 2;
    private volatile java.lang.Object name_;
    /**
     * <code>string name = 2;</code>
     */
    public java.lang.String getName() {
      java.lang.Object ref = name_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        name_ = s;
        return s;
      }
    }
    /**
     * <code>string name = 2;</code>
     */
    public com.google.protobuf.ByteString
        getNameBytes() {
      java.lang.Object ref = name_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        name_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int HP_FIELD_NUMBER = 3;
    private long hp_;
    /**
     * <code>int64 hp = 3;</code>
     */
    public long getHp() {
      return hp_;
    }

    public static final int SPELL_FIELD_NUMBER = 4;
    private java.util.List<com.ljh.gamedemo.proto.protoc.SpellProto.Spell> spell_;
    /**
     * <code>repeated .Spell spell = 4;</code>
     */
    public java.util.List<com.ljh.gamedemo.proto.protoc.SpellProto.Spell> getSpellList() {
      return spell_;
    }
    /**
     * <code>repeated .Spell spell = 4;</code>
     */
    public java.util.List<? extends com.ljh.gamedemo.proto.protoc.SpellProto.SpellOrBuilder> 
        getSpellOrBuilderList() {
      return spell_;
    }
    /**
     * <code>repeated .Spell spell = 4;</code>
     */
    public int getSpellCount() {
      return spell_.size();
    }
    /**
     * <code>repeated .Spell spell = 4;</code>
     */
    public com.ljh.gamedemo.proto.protoc.SpellProto.Spell getSpell(int index) {
      return spell_.get(index);
    }
    /**
     * <code>repeated .Spell spell = 4;</code>
     */
    public com.ljh.gamedemo.proto.protoc.SpellProto.SpellOrBuilder getSpellOrBuilder(
        int index) {
      return spell_.get(index);
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (id_ != 0L) {
        output.writeInt64(1, id_);
      }
      if (!getNameBytes().isEmpty()) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 2, name_);
      }
      if (hp_ != 0L) {
        output.writeInt64(3, hp_);
      }
      for (int i = 0; i < spell_.size(); i++) {
        output.writeMessage(4, spell_.get(i));
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (id_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(1, id_);
      }
      if (!getNameBytes().isEmpty()) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, name_);
      }
      if (hp_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(3, hp_);
      }
      for (int i = 0; i < spell_.size(); i++) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(4, spell_.get(i));
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof com.ljh.gamedemo.proto.protoc.BossProto.Boss)) {
        return super.equals(obj);
      }
      com.ljh.gamedemo.proto.protoc.BossProto.Boss other = (com.ljh.gamedemo.proto.protoc.BossProto.Boss) obj;

      if (getId()
          != other.getId()) return false;
      if (!getName()
          .equals(other.getName())) return false;
      if (getHp()
          != other.getHp()) return false;
      if (!getSpellList()
          .equals(other.getSpellList())) return false;
      if (!unknownFields.equals(other.unknownFields)) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + ID_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getId());
      hash = (37 * hash) + NAME_FIELD_NUMBER;
      hash = (53 * hash) + getName().hashCode();
      hash = (37 * hash) + HP_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getHp());
      if (getSpellCount() > 0) {
        hash = (37 * hash) + SPELL_FIELD_NUMBER;
        hash = (53 * hash) + getSpellList().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static com.ljh.gamedemo.proto.protoc.BossProto.Boss parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.ljh.gamedemo.proto.protoc.BossProto.Boss parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.ljh.gamedemo.proto.protoc.BossProto.Boss parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.ljh.gamedemo.proto.protoc.BossProto.Boss parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.ljh.gamedemo.proto.protoc.BossProto.Boss parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.ljh.gamedemo.proto.protoc.BossProto.Boss parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.ljh.gamedemo.proto.protoc.BossProto.Boss parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.ljh.gamedemo.proto.protoc.BossProto.Boss parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.ljh.gamedemo.proto.protoc.BossProto.Boss parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static com.ljh.gamedemo.proto.protoc.BossProto.Boss parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.ljh.gamedemo.proto.protoc.BossProto.Boss parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.ljh.gamedemo.proto.protoc.BossProto.Boss parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(com.ljh.gamedemo.proto.protoc.BossProto.Boss prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code Boss}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:Boss)
        com.ljh.gamedemo.proto.protoc.BossProto.BossOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.ljh.gamedemo.proto.protoc.BossProto.internal_static_Boss_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.ljh.gamedemo.proto.protoc.BossProto.internal_static_Boss_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.ljh.gamedemo.proto.protoc.BossProto.Boss.class, com.ljh.gamedemo.proto.protoc.BossProto.Boss.Builder.class);
      }

      // Construct using com.ljh.gamedemo.proto.protoc.BossProto.Boss.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
          getSpellFieldBuilder();
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        id_ = 0L;

        name_ = "";

        hp_ = 0L;

        if (spellBuilder_ == null) {
          spell_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000001);
        } else {
          spellBuilder_.clear();
        }
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.ljh.gamedemo.proto.protoc.BossProto.internal_static_Boss_descriptor;
      }

      @java.lang.Override
      public com.ljh.gamedemo.proto.protoc.BossProto.Boss getDefaultInstanceForType() {
        return com.ljh.gamedemo.proto.protoc.BossProto.Boss.getDefaultInstance();
      }

      @java.lang.Override
      public com.ljh.gamedemo.proto.protoc.BossProto.Boss build() {
        com.ljh.gamedemo.proto.protoc.BossProto.Boss result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public com.ljh.gamedemo.proto.protoc.BossProto.Boss buildPartial() {
        com.ljh.gamedemo.proto.protoc.BossProto.Boss result = new com.ljh.gamedemo.proto.protoc.BossProto.Boss(this);
        int from_bitField0_ = bitField0_;
        result.id_ = id_;
        result.name_ = name_;
        result.hp_ = hp_;
        if (spellBuilder_ == null) {
          if (((bitField0_ & 0x00000001) != 0)) {
            spell_ = java.util.Collections.unmodifiableList(spell_);
            bitField0_ = (bitField0_ & ~0x00000001);
          }
          result.spell_ = spell_;
        } else {
          result.spell_ = spellBuilder_.build();
        }
        onBuilt();
        return result;
      }

      @java.lang.Override
      public Builder clone() {
        return super.clone();
      }
      @java.lang.Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.setField(field, value);
      }
      @java.lang.Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @java.lang.Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @java.lang.Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, java.lang.Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @java.lang.Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.addRepeatedField(field, value);
      }
      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.ljh.gamedemo.proto.protoc.BossProto.Boss) {
          return mergeFrom((com.ljh.gamedemo.proto.protoc.BossProto.Boss)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.ljh.gamedemo.proto.protoc.BossProto.Boss other) {
        if (other == com.ljh.gamedemo.proto.protoc.BossProto.Boss.getDefaultInstance()) return this;
        if (other.getId() != 0L) {
          setId(other.getId());
        }
        if (!other.getName().isEmpty()) {
          name_ = other.name_;
          onChanged();
        }
        if (other.getHp() != 0L) {
          setHp(other.getHp());
        }
        if (spellBuilder_ == null) {
          if (!other.spell_.isEmpty()) {
            if (spell_.isEmpty()) {
              spell_ = other.spell_;
              bitField0_ = (bitField0_ & ~0x00000001);
            } else {
              ensureSpellIsMutable();
              spell_.addAll(other.spell_);
            }
            onChanged();
          }
        } else {
          if (!other.spell_.isEmpty()) {
            if (spellBuilder_.isEmpty()) {
              spellBuilder_.dispose();
              spellBuilder_ = null;
              spell_ = other.spell_;
              bitField0_ = (bitField0_ & ~0x00000001);
              spellBuilder_ = 
                com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                   getSpellFieldBuilder() : null;
            } else {
              spellBuilder_.addAllMessages(other.spell_);
            }
          }
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.ljh.gamedemo.proto.protoc.BossProto.Boss parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.ljh.gamedemo.proto.protoc.BossProto.Boss) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private long id_ ;
      /**
       * <code>int64 id = 1;</code>
       */
      public long getId() {
        return id_;
      }
      /**
       * <code>int64 id = 1;</code>
       */
      public Builder setId(long value) {
        
        id_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int64 id = 1;</code>
       */
      public Builder clearId() {
        
        id_ = 0L;
        onChanged();
        return this;
      }

      private java.lang.Object name_ = "";
      /**
       * <code>string name = 2;</code>
       */
      public java.lang.String getName() {
        java.lang.Object ref = name_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          name_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string name = 2;</code>
       */
      public com.google.protobuf.ByteString
          getNameBytes() {
        java.lang.Object ref = name_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          name_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string name = 2;</code>
       */
      public Builder setName(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        name_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string name = 2;</code>
       */
      public Builder clearName() {
        
        name_ = getDefaultInstance().getName();
        onChanged();
        return this;
      }
      /**
       * <code>string name = 2;</code>
       */
      public Builder setNameBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        name_ = value;
        onChanged();
        return this;
      }

      private long hp_ ;
      /**
       * <code>int64 hp = 3;</code>
       */
      public long getHp() {
        return hp_;
      }
      /**
       * <code>int64 hp = 3;</code>
       */
      public Builder setHp(long value) {
        
        hp_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int64 hp = 3;</code>
       */
      public Builder clearHp() {
        
        hp_ = 0L;
        onChanged();
        return this;
      }

      private java.util.List<com.ljh.gamedemo.proto.protoc.SpellProto.Spell> spell_ =
        java.util.Collections.emptyList();
      private void ensureSpellIsMutable() {
        if (!((bitField0_ & 0x00000001) != 0)) {
          spell_ = new java.util.ArrayList<com.ljh.gamedemo.proto.protoc.SpellProto.Spell>(spell_);
          bitField0_ |= 0x00000001;
         }
      }

      private com.google.protobuf.RepeatedFieldBuilderV3<
          com.ljh.gamedemo.proto.protoc.SpellProto.Spell, com.ljh.gamedemo.proto.protoc.SpellProto.Spell.Builder, com.ljh.gamedemo.proto.protoc.SpellProto.SpellOrBuilder> spellBuilder_;

      /**
       * <code>repeated .Spell spell = 4;</code>
       */
      public java.util.List<com.ljh.gamedemo.proto.protoc.SpellProto.Spell> getSpellList() {
        if (spellBuilder_ == null) {
          return java.util.Collections.unmodifiableList(spell_);
        } else {
          return spellBuilder_.getMessageList();
        }
      }
      /**
       * <code>repeated .Spell spell = 4;</code>
       */
      public int getSpellCount() {
        if (spellBuilder_ == null) {
          return spell_.size();
        } else {
          return spellBuilder_.getCount();
        }
      }
      /**
       * <code>repeated .Spell spell = 4;</code>
       */
      public com.ljh.gamedemo.proto.protoc.SpellProto.Spell getSpell(int index) {
        if (spellBuilder_ == null) {
          return spell_.get(index);
        } else {
          return spellBuilder_.getMessage(index);
        }
      }
      /**
       * <code>repeated .Spell spell = 4;</code>
       */
      public Builder setSpell(
          int index, com.ljh.gamedemo.proto.protoc.SpellProto.Spell value) {
        if (spellBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureSpellIsMutable();
          spell_.set(index, value);
          onChanged();
        } else {
          spellBuilder_.setMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .Spell spell = 4;</code>
       */
      public Builder setSpell(
          int index, com.ljh.gamedemo.proto.protoc.SpellProto.Spell.Builder builderForValue) {
        if (spellBuilder_ == null) {
          ensureSpellIsMutable();
          spell_.set(index, builderForValue.build());
          onChanged();
        } else {
          spellBuilder_.setMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .Spell spell = 4;</code>
       */
      public Builder addSpell(com.ljh.gamedemo.proto.protoc.SpellProto.Spell value) {
        if (spellBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureSpellIsMutable();
          spell_.add(value);
          onChanged();
        } else {
          spellBuilder_.addMessage(value);
        }
        return this;
      }
      /**
       * <code>repeated .Spell spell = 4;</code>
       */
      public Builder addSpell(
          int index, com.ljh.gamedemo.proto.protoc.SpellProto.Spell value) {
        if (spellBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureSpellIsMutable();
          spell_.add(index, value);
          onChanged();
        } else {
          spellBuilder_.addMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .Spell spell = 4;</code>
       */
      public Builder addSpell(
          com.ljh.gamedemo.proto.protoc.SpellProto.Spell.Builder builderForValue) {
        if (spellBuilder_ == null) {
          ensureSpellIsMutable();
          spell_.add(builderForValue.build());
          onChanged();
        } else {
          spellBuilder_.addMessage(builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .Spell spell = 4;</code>
       */
      public Builder addSpell(
          int index, com.ljh.gamedemo.proto.protoc.SpellProto.Spell.Builder builderForValue) {
        if (spellBuilder_ == null) {
          ensureSpellIsMutable();
          spell_.add(index, builderForValue.build());
          onChanged();
        } else {
          spellBuilder_.addMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .Spell spell = 4;</code>
       */
      public Builder addAllSpell(
          java.lang.Iterable<? extends com.ljh.gamedemo.proto.protoc.SpellProto.Spell> values) {
        if (spellBuilder_ == null) {
          ensureSpellIsMutable();
          com.google.protobuf.AbstractMessageLite.Builder.addAll(
              values, spell_);
          onChanged();
        } else {
          spellBuilder_.addAllMessages(values);
        }
        return this;
      }
      /**
       * <code>repeated .Spell spell = 4;</code>
       */
      public Builder clearSpell() {
        if (spellBuilder_ == null) {
          spell_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000001);
          onChanged();
        } else {
          spellBuilder_.clear();
        }
        return this;
      }
      /**
       * <code>repeated .Spell spell = 4;</code>
       */
      public Builder removeSpell(int index) {
        if (spellBuilder_ == null) {
          ensureSpellIsMutable();
          spell_.remove(index);
          onChanged();
        } else {
          spellBuilder_.remove(index);
        }
        return this;
      }
      /**
       * <code>repeated .Spell spell = 4;</code>
       */
      public com.ljh.gamedemo.proto.protoc.SpellProto.Spell.Builder getSpellBuilder(
          int index) {
        return getSpellFieldBuilder().getBuilder(index);
      }
      /**
       * <code>repeated .Spell spell = 4;</code>
       */
      public com.ljh.gamedemo.proto.protoc.SpellProto.SpellOrBuilder getSpellOrBuilder(
          int index) {
        if (spellBuilder_ == null) {
          return spell_.get(index);  } else {
          return spellBuilder_.getMessageOrBuilder(index);
        }
      }
      /**
       * <code>repeated .Spell spell = 4;</code>
       */
      public java.util.List<? extends com.ljh.gamedemo.proto.protoc.SpellProto.SpellOrBuilder> 
           getSpellOrBuilderList() {
        if (spellBuilder_ != null) {
          return spellBuilder_.getMessageOrBuilderList();
        } else {
          return java.util.Collections.unmodifiableList(spell_);
        }
      }
      /**
       * <code>repeated .Spell spell = 4;</code>
       */
      public com.ljh.gamedemo.proto.protoc.SpellProto.Spell.Builder addSpellBuilder() {
        return getSpellFieldBuilder().addBuilder(
            com.ljh.gamedemo.proto.protoc.SpellProto.Spell.getDefaultInstance());
      }
      /**
       * <code>repeated .Spell spell = 4;</code>
       */
      public com.ljh.gamedemo.proto.protoc.SpellProto.Spell.Builder addSpellBuilder(
          int index) {
        return getSpellFieldBuilder().addBuilder(
            index, com.ljh.gamedemo.proto.protoc.SpellProto.Spell.getDefaultInstance());
      }
      /**
       * <code>repeated .Spell spell = 4;</code>
       */
      public java.util.List<com.ljh.gamedemo.proto.protoc.SpellProto.Spell.Builder> 
           getSpellBuilderList() {
        return getSpellFieldBuilder().getBuilderList();
      }
      private com.google.protobuf.RepeatedFieldBuilderV3<
          com.ljh.gamedemo.proto.protoc.SpellProto.Spell, com.ljh.gamedemo.proto.protoc.SpellProto.Spell.Builder, com.ljh.gamedemo.proto.protoc.SpellProto.SpellOrBuilder> 
          getSpellFieldBuilder() {
        if (spellBuilder_ == null) {
          spellBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
              com.ljh.gamedemo.proto.protoc.SpellProto.Spell, com.ljh.gamedemo.proto.protoc.SpellProto.Spell.Builder, com.ljh.gamedemo.proto.protoc.SpellProto.SpellOrBuilder>(
                  spell_,
                  ((bitField0_ & 0x00000001) != 0),
                  getParentForChildren(),
                  isClean());
          spell_ = null;
        }
        return spellBuilder_;
      }
      @java.lang.Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @java.lang.Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:Boss)
    }

    // @@protoc_insertion_point(class_scope:Boss)
    private static final com.ljh.gamedemo.proto.protoc.BossProto.Boss DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new com.ljh.gamedemo.proto.protoc.BossProto.Boss();
    }

    public static com.ljh.gamedemo.proto.protoc.BossProto.Boss getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<Boss>
        PARSER = new com.google.protobuf.AbstractParser<Boss>() {
      @java.lang.Override
      public Boss parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new Boss(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<Boss> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<Boss> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public com.ljh.gamedemo.proto.protoc.BossProto.Boss getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_Boss_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_Boss_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\nBoss.proto\032\013Spell.proto\"C\n\004Boss\022\n\n\002id\030" +
      "\001 \001(\003\022\014\n\004name\030\002 \001(\t\022\n\n\002hp\030\003 \001(\003\022\025\n\005spell" +
      "\030\004 \003(\0132\006.SpellB*\n\035com.ljh.gamedemo.proto" +
      ".protocB\tBossProtob\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.ljh.gamedemo.proto.protoc.SpellProto.getDescriptor(),
        });
    internal_static_Boss_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_Boss_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_Boss_descriptor,
        new java.lang.String[] { "Id", "Name", "Hp", "Spell", });
    com.ljh.gamedemo.proto.protoc.SpellProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}