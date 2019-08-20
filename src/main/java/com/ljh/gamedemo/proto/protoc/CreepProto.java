// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: src/main/java/com/ljh/gamedemo/proto/Creep.proto

package com.ljh.gamedemo.proto.protoc;

public final class CreepProto {
  private CreepProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface CreepOrBuilder extends
      // @@protoc_insertion_point(interface_extends:Creep)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>int64 creepId = 1;</code>
     */
    long getCreepId();

    /**
     * <code>int32 type = 2;</code>
     */
    int getType();

    /**
     * <code>string name = 3;</code>
     */
    java.lang.String getName();
    /**
     * <code>string name = 3;</code>
     */
    com.google.protobuf.ByteString
        getNameBytes();

    /**
     * <code>int32 num = 4;</code>
     */
    int getNum();

    /**
     * <code>int32 level = 5;</code>
     */
    int getLevel();

    /**
     * <code>int32 hp = 6;</code>
     */
    int getHp();

    /**
     * <code>int32 damage = 7;</code>
     */
    int getDamage();

    /**
     * <code>int32 maxHp = 8;</code>
     */
    int getMaxHp();
  }
  /**
   * Protobuf type {@code Creep}
   */
  public  static final class Creep extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:Creep)
      CreepOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use Creep.newBuilder() to construct.
    private Creep(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private Creep() {
      name_ = "";
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new Creep();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private Creep(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
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

              creepId_ = input.readInt64();
              break;
            }
            case 16: {

              type_ = input.readInt32();
              break;
            }
            case 26: {
              java.lang.String s = input.readStringRequireUtf8();

              name_ = s;
              break;
            }
            case 32: {

              num_ = input.readInt32();
              break;
            }
            case 40: {

              level_ = input.readInt32();
              break;
            }
            case 48: {

              hp_ = input.readInt32();
              break;
            }
            case 56: {

              damage_ = input.readInt32();
              break;
            }
            case 64: {

              maxHp_ = input.readInt32();
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
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.ljh.gamedemo.proto.protoc.CreepProto.internal_static_Creep_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.ljh.gamedemo.proto.protoc.CreepProto.internal_static_Creep_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.ljh.gamedemo.proto.protoc.CreepProto.Creep.class, com.ljh.gamedemo.proto.protoc.CreepProto.Creep.Builder.class);
    }

    public static final int CREEPID_FIELD_NUMBER = 1;
    private long creepId_;
    /**
     * <code>int64 creepId = 1;</code>
     */
    public long getCreepId() {
      return creepId_;
    }

    public static final int TYPE_FIELD_NUMBER = 2;
    private int type_;
    /**
     * <code>int32 type = 2;</code>
     */
    public int getType() {
      return type_;
    }

    public static final int NAME_FIELD_NUMBER = 3;
    private volatile java.lang.Object name_;
    /**
     * <code>string name = 3;</code>
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
     * <code>string name = 3;</code>
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

    public static final int NUM_FIELD_NUMBER = 4;
    private int num_;
    /**
     * <code>int32 num = 4;</code>
     */
    public int getNum() {
      return num_;
    }

    public static final int LEVEL_FIELD_NUMBER = 5;
    private int level_;
    /**
     * <code>int32 level = 5;</code>
     */
    public int getLevel() {
      return level_;
    }

    public static final int HP_FIELD_NUMBER = 6;
    private int hp_;
    /**
     * <code>int32 hp = 6;</code>
     */
    public int getHp() {
      return hp_;
    }

    public static final int DAMAGE_FIELD_NUMBER = 7;
    private int damage_;
    /**
     * <code>int32 damage = 7;</code>
     */
    public int getDamage() {
      return damage_;
    }

    public static final int MAXHP_FIELD_NUMBER = 8;
    private int maxHp_;
    /**
     * <code>int32 maxHp = 8;</code>
     */
    public int getMaxHp() {
      return maxHp_;
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
      if (creepId_ != 0L) {
        output.writeInt64(1, creepId_);
      }
      if (type_ != 0) {
        output.writeInt32(2, type_);
      }
      if (!getNameBytes().isEmpty()) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 3, name_);
      }
      if (num_ != 0) {
        output.writeInt32(4, num_);
      }
      if (level_ != 0) {
        output.writeInt32(5, level_);
      }
      if (hp_ != 0) {
        output.writeInt32(6, hp_);
      }
      if (damage_ != 0) {
        output.writeInt32(7, damage_);
      }
      if (maxHp_ != 0) {
        output.writeInt32(8, maxHp_);
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (creepId_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(1, creepId_);
      }
      if (type_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(2, type_);
      }
      if (!getNameBytes().isEmpty()) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, name_);
      }
      if (num_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(4, num_);
      }
      if (level_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(5, level_);
      }
      if (hp_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(6, hp_);
      }
      if (damage_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(7, damage_);
      }
      if (maxHp_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(8, maxHp_);
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
      if (!(obj instanceof com.ljh.gamedemo.proto.protoc.CreepProto.Creep)) {
        return super.equals(obj);
      }
      com.ljh.gamedemo.proto.protoc.CreepProto.Creep other = (com.ljh.gamedemo.proto.protoc.CreepProto.Creep) obj;

      if (getCreepId()
          != other.getCreepId()) return false;
      if (getType()
          != other.getType()) return false;
      if (!getName()
          .equals(other.getName())) return false;
      if (getNum()
          != other.getNum()) return false;
      if (getLevel()
          != other.getLevel()) return false;
      if (getHp()
          != other.getHp()) return false;
      if (getDamage()
          != other.getDamage()) return false;
      if (getMaxHp()
          != other.getMaxHp()) return false;
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
      hash = (37 * hash) + CREEPID_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getCreepId());
      hash = (37 * hash) + TYPE_FIELD_NUMBER;
      hash = (53 * hash) + getType();
      hash = (37 * hash) + NAME_FIELD_NUMBER;
      hash = (53 * hash) + getName().hashCode();
      hash = (37 * hash) + NUM_FIELD_NUMBER;
      hash = (53 * hash) + getNum();
      hash = (37 * hash) + LEVEL_FIELD_NUMBER;
      hash = (53 * hash) + getLevel();
      hash = (37 * hash) + HP_FIELD_NUMBER;
      hash = (53 * hash) + getHp();
      hash = (37 * hash) + DAMAGE_FIELD_NUMBER;
      hash = (53 * hash) + getDamage();
      hash = (37 * hash) + MAXHP_FIELD_NUMBER;
      hash = (53 * hash) + getMaxHp();
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static com.ljh.gamedemo.proto.protoc.CreepProto.Creep parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.ljh.gamedemo.proto.protoc.CreepProto.Creep parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.ljh.gamedemo.proto.protoc.CreepProto.Creep parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.ljh.gamedemo.proto.protoc.CreepProto.Creep parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.ljh.gamedemo.proto.protoc.CreepProto.Creep parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.ljh.gamedemo.proto.protoc.CreepProto.Creep parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.ljh.gamedemo.proto.protoc.CreepProto.Creep parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.ljh.gamedemo.proto.protoc.CreepProto.Creep parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.ljh.gamedemo.proto.protoc.CreepProto.Creep parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static com.ljh.gamedemo.proto.protoc.CreepProto.Creep parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.ljh.gamedemo.proto.protoc.CreepProto.Creep parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.ljh.gamedemo.proto.protoc.CreepProto.Creep parseFrom(
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
    public static Builder newBuilder(com.ljh.gamedemo.proto.protoc.CreepProto.Creep prototype) {
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
     * Protobuf type {@code Creep}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:Creep)
        com.ljh.gamedemo.proto.protoc.CreepProto.CreepOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.ljh.gamedemo.proto.protoc.CreepProto.internal_static_Creep_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.ljh.gamedemo.proto.protoc.CreepProto.internal_static_Creep_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.ljh.gamedemo.proto.protoc.CreepProto.Creep.class, com.ljh.gamedemo.proto.protoc.CreepProto.Creep.Builder.class);
      }

      // Construct using com.ljh.gamedemo.proto.protoc.CreepProto.Creep.newBuilder()
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
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        creepId_ = 0L;

        type_ = 0;

        name_ = "";

        num_ = 0;

        level_ = 0;

        hp_ = 0;

        damage_ = 0;

        maxHp_ = 0;

        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.ljh.gamedemo.proto.protoc.CreepProto.internal_static_Creep_descriptor;
      }

      @java.lang.Override
      public com.ljh.gamedemo.proto.protoc.CreepProto.Creep getDefaultInstanceForType() {
        return com.ljh.gamedemo.proto.protoc.CreepProto.Creep.getDefaultInstance();
      }

      @java.lang.Override
      public com.ljh.gamedemo.proto.protoc.CreepProto.Creep build() {
        com.ljh.gamedemo.proto.protoc.CreepProto.Creep result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public com.ljh.gamedemo.proto.protoc.CreepProto.Creep buildPartial() {
        com.ljh.gamedemo.proto.protoc.CreepProto.Creep result = new com.ljh.gamedemo.proto.protoc.CreepProto.Creep(this);
        result.creepId_ = creepId_;
        result.type_ = type_;
        result.name_ = name_;
        result.num_ = num_;
        result.level_ = level_;
        result.hp_ = hp_;
        result.damage_ = damage_;
        result.maxHp_ = maxHp_;
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
        if (other instanceof com.ljh.gamedemo.proto.protoc.CreepProto.Creep) {
          return mergeFrom((com.ljh.gamedemo.proto.protoc.CreepProto.Creep)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.ljh.gamedemo.proto.protoc.CreepProto.Creep other) {
        if (other == com.ljh.gamedemo.proto.protoc.CreepProto.Creep.getDefaultInstance()) return this;
        if (other.getCreepId() != 0L) {
          setCreepId(other.getCreepId());
        }
        if (other.getType() != 0) {
          setType(other.getType());
        }
        if (!other.getName().isEmpty()) {
          name_ = other.name_;
          onChanged();
        }
        if (other.getNum() != 0) {
          setNum(other.getNum());
        }
        if (other.getLevel() != 0) {
          setLevel(other.getLevel());
        }
        if (other.getHp() != 0) {
          setHp(other.getHp());
        }
        if (other.getDamage() != 0) {
          setDamage(other.getDamage());
        }
        if (other.getMaxHp() != 0) {
          setMaxHp(other.getMaxHp());
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
        com.ljh.gamedemo.proto.protoc.CreepProto.Creep parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.ljh.gamedemo.proto.protoc.CreepProto.Creep) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private long creepId_ ;
      /**
       * <code>int64 creepId = 1;</code>
       */
      public long getCreepId() {
        return creepId_;
      }
      /**
       * <code>int64 creepId = 1;</code>
       */
      public Builder setCreepId(long value) {
        
        creepId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int64 creepId = 1;</code>
       */
      public Builder clearCreepId() {
        
        creepId_ = 0L;
        onChanged();
        return this;
      }

      private int type_ ;
      /**
       * <code>int32 type = 2;</code>
       */
      public int getType() {
        return type_;
      }
      /**
       * <code>int32 type = 2;</code>
       */
      public Builder setType(int value) {
        
        type_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 type = 2;</code>
       */
      public Builder clearType() {
        
        type_ = 0;
        onChanged();
        return this;
      }

      private java.lang.Object name_ = "";
      /**
       * <code>string name = 3;</code>
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
       * <code>string name = 3;</code>
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
       * <code>string name = 3;</code>
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
       * <code>string name = 3;</code>
       */
      public Builder clearName() {
        
        name_ = getDefaultInstance().getName();
        onChanged();
        return this;
      }
      /**
       * <code>string name = 3;</code>
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

      private int num_ ;
      /**
       * <code>int32 num = 4;</code>
       */
      public int getNum() {
        return num_;
      }
      /**
       * <code>int32 num = 4;</code>
       */
      public Builder setNum(int value) {
        
        num_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 num = 4;</code>
       */
      public Builder clearNum() {
        
        num_ = 0;
        onChanged();
        return this;
      }

      private int level_ ;
      /**
       * <code>int32 level = 5;</code>
       */
      public int getLevel() {
        return level_;
      }
      /**
       * <code>int32 level = 5;</code>
       */
      public Builder setLevel(int value) {
        
        level_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 level = 5;</code>
       */
      public Builder clearLevel() {
        
        level_ = 0;
        onChanged();
        return this;
      }

      private int hp_ ;
      /**
       * <code>int32 hp = 6;</code>
       */
      public int getHp() {
        return hp_;
      }
      /**
       * <code>int32 hp = 6;</code>
       */
      public Builder setHp(int value) {
        
        hp_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 hp = 6;</code>
       */
      public Builder clearHp() {
        
        hp_ = 0;
        onChanged();
        return this;
      }

      private int damage_ ;
      /**
       * <code>int32 damage = 7;</code>
       */
      public int getDamage() {
        return damage_;
      }
      /**
       * <code>int32 damage = 7;</code>
       */
      public Builder setDamage(int value) {
        
        damage_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 damage = 7;</code>
       */
      public Builder clearDamage() {
        
        damage_ = 0;
        onChanged();
        return this;
      }

      private int maxHp_ ;
      /**
       * <code>int32 maxHp = 8;</code>
       */
      public int getMaxHp() {
        return maxHp_;
      }
      /**
       * <code>int32 maxHp = 8;</code>
       */
      public Builder setMaxHp(int value) {
        
        maxHp_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 maxHp = 8;</code>
       */
      public Builder clearMaxHp() {
        
        maxHp_ = 0;
        onChanged();
        return this;
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


      // @@protoc_insertion_point(builder_scope:Creep)
    }

    // @@protoc_insertion_point(class_scope:Creep)
    private static final com.ljh.gamedemo.proto.protoc.CreepProto.Creep DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new com.ljh.gamedemo.proto.protoc.CreepProto.Creep();
    }

    public static com.ljh.gamedemo.proto.protoc.CreepProto.Creep getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<Creep>
        PARSER = new com.google.protobuf.AbstractParser<Creep>() {
      @java.lang.Override
      public Creep parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new Creep(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<Creep> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<Creep> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public com.ljh.gamedemo.proto.protoc.CreepProto.Creep getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_Creep_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_Creep_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n0src/main/java/com/ljh/gamedemo/proto/C" +
      "reep.proto\"{\n\005Creep\022\017\n\007creepId\030\001 \001(\003\022\014\n\004" +
      "type\030\002 \001(\005\022\014\n\004name\030\003 \001(\t\022\013\n\003num\030\004 \001(\005\022\r\n" +
      "\005level\030\005 \001(\005\022\n\n\002hp\030\006 \001(\005\022\016\n\006damage\030\007 \001(\005" +
      "\022\r\n\005maxHp\030\010 \001(\005B+\n\035com.ljh.gamedemo.prot" +
      "o.protocB\nCreepProtob\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_Creep_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_Creep_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_Creep_descriptor,
        new java.lang.String[] { "CreepId", "Type", "Name", "Num", "Level", "Hp", "Damage", "MaxHp", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
