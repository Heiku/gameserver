// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: src/main/java/com/ljh/gamedemo/proto/Role.proto

package com.ljh.gamedemo.proto.protoc;

public final class RoleProto {
  private RoleProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface RoleOrBuilder extends
      // @@protoc_insertion_point(interface_extends:Role)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>int64 roleId = 1;</code>
     */
    long getRoleId();

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
     * <code>int32 type = 3;</code>
     */
    int getType();

    /**
     * <code>int32 level = 4;</code>
     */
    int getLevel();

    /**
     * <code>int32 alive = 5;</code>
     */
    int getAlive();

    /**
     * <code>int32 hp = 6;</code>
     */
    int getHp();

    /**
     * <code>int32 mp = 7;</code>
     */
    int getMp();

    /**
     * <code>int32 gold = 8;</code>
     */
    int getGold();
  }
  /**
   * Protobuf type {@code Role}
   */
  public  static final class Role extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:Role)
      RoleOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use Role.newBuilder() to construct.
    private Role(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private Role() {
      name_ = "";
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new Role();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private Role(
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

              roleId_ = input.readInt64();
              break;
            }
            case 18: {
              java.lang.String s = input.readStringRequireUtf8();

              name_ = s;
              break;
            }
            case 24: {

              type_ = input.readInt32();
              break;
            }
            case 32: {

              level_ = input.readInt32();
              break;
            }
            case 40: {

              alive_ = input.readInt32();
              break;
            }
            case 48: {

              hp_ = input.readInt32();
              break;
            }
            case 56: {

              mp_ = input.readInt32();
              break;
            }
            case 64: {

              gold_ = input.readInt32();
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
      return com.ljh.gamedemo.proto.protoc.RoleProto.internal_static_Role_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.ljh.gamedemo.proto.protoc.RoleProto.internal_static_Role_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.ljh.gamedemo.proto.protoc.RoleProto.Role.class, com.ljh.gamedemo.proto.protoc.RoleProto.Role.Builder.class);
    }

    public static final int ROLEID_FIELD_NUMBER = 1;
    private long roleId_;
    /**
     * <code>int64 roleId = 1;</code>
     */
    public long getRoleId() {
      return roleId_;
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

    public static final int TYPE_FIELD_NUMBER = 3;
    private int type_;
    /**
     * <code>int32 type = 3;</code>
     */
    public int getType() {
      return type_;
    }

    public static final int LEVEL_FIELD_NUMBER = 4;
    private int level_;
    /**
     * <code>int32 level = 4;</code>
     */
    public int getLevel() {
      return level_;
    }

    public static final int ALIVE_FIELD_NUMBER = 5;
    private int alive_;
    /**
     * <code>int32 alive = 5;</code>
     */
    public int getAlive() {
      return alive_;
    }

    public static final int HP_FIELD_NUMBER = 6;
    private int hp_;
    /**
     * <code>int32 hp = 6;</code>
     */
    public int getHp() {
      return hp_;
    }

    public static final int MP_FIELD_NUMBER = 7;
    private int mp_;
    /**
     * <code>int32 mp = 7;</code>
     */
    public int getMp() {
      return mp_;
    }

    public static final int GOLD_FIELD_NUMBER = 8;
    private int gold_;
    /**
     * <code>int32 gold = 8;</code>
     */
    public int getGold() {
      return gold_;
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
      if (roleId_ != 0L) {
        output.writeInt64(1, roleId_);
      }
      if (!getNameBytes().isEmpty()) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 2, name_);
      }
      if (type_ != 0) {
        output.writeInt32(3, type_);
      }
      if (level_ != 0) {
        output.writeInt32(4, level_);
      }
      if (alive_ != 0) {
        output.writeInt32(5, alive_);
      }
      if (hp_ != 0) {
        output.writeInt32(6, hp_);
      }
      if (mp_ != 0) {
        output.writeInt32(7, mp_);
      }
      if (gold_ != 0) {
        output.writeInt32(8, gold_);
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (roleId_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(1, roleId_);
      }
      if (!getNameBytes().isEmpty()) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, name_);
      }
      if (type_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(3, type_);
      }
      if (level_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(4, level_);
      }
      if (alive_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(5, alive_);
      }
      if (hp_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(6, hp_);
      }
      if (mp_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(7, mp_);
      }
      if (gold_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(8, gold_);
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
      if (!(obj instanceof com.ljh.gamedemo.proto.protoc.RoleProto.Role)) {
        return super.equals(obj);
      }
      com.ljh.gamedemo.proto.protoc.RoleProto.Role other = (com.ljh.gamedemo.proto.protoc.RoleProto.Role) obj;

      if (getRoleId()
          != other.getRoleId()) return false;
      if (!getName()
          .equals(other.getName())) return false;
      if (getType()
          != other.getType()) return false;
      if (getLevel()
          != other.getLevel()) return false;
      if (getAlive()
          != other.getAlive()) return false;
      if (getHp()
          != other.getHp()) return false;
      if (getMp()
          != other.getMp()) return false;
      if (getGold()
          != other.getGold()) return false;
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
      hash = (37 * hash) + ROLEID_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getRoleId());
      hash = (37 * hash) + NAME_FIELD_NUMBER;
      hash = (53 * hash) + getName().hashCode();
      hash = (37 * hash) + TYPE_FIELD_NUMBER;
      hash = (53 * hash) + getType();
      hash = (37 * hash) + LEVEL_FIELD_NUMBER;
      hash = (53 * hash) + getLevel();
      hash = (37 * hash) + ALIVE_FIELD_NUMBER;
      hash = (53 * hash) + getAlive();
      hash = (37 * hash) + HP_FIELD_NUMBER;
      hash = (53 * hash) + getHp();
      hash = (37 * hash) + MP_FIELD_NUMBER;
      hash = (53 * hash) + getMp();
      hash = (37 * hash) + GOLD_FIELD_NUMBER;
      hash = (53 * hash) + getGold();
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static com.ljh.gamedemo.proto.protoc.RoleProto.Role parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.ljh.gamedemo.proto.protoc.RoleProto.Role parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.ljh.gamedemo.proto.protoc.RoleProto.Role parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.ljh.gamedemo.proto.protoc.RoleProto.Role parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.ljh.gamedemo.proto.protoc.RoleProto.Role parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.ljh.gamedemo.proto.protoc.RoleProto.Role parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.ljh.gamedemo.proto.protoc.RoleProto.Role parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.ljh.gamedemo.proto.protoc.RoleProto.Role parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.ljh.gamedemo.proto.protoc.RoleProto.Role parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static com.ljh.gamedemo.proto.protoc.RoleProto.Role parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.ljh.gamedemo.proto.protoc.RoleProto.Role parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.ljh.gamedemo.proto.protoc.RoleProto.Role parseFrom(
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
    public static Builder newBuilder(com.ljh.gamedemo.proto.protoc.RoleProto.Role prototype) {
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
     * Protobuf type {@code Role}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:Role)
        com.ljh.gamedemo.proto.protoc.RoleProto.RoleOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.ljh.gamedemo.proto.protoc.RoleProto.internal_static_Role_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.ljh.gamedemo.proto.protoc.RoleProto.internal_static_Role_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.ljh.gamedemo.proto.protoc.RoleProto.Role.class, com.ljh.gamedemo.proto.protoc.RoleProto.Role.Builder.class);
      }

      // Construct using com.ljh.gamedemo.proto.protoc.RoleProto.Role.newBuilder()
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
        roleId_ = 0L;

        name_ = "";

        type_ = 0;

        level_ = 0;

        alive_ = 0;

        hp_ = 0;

        mp_ = 0;

        gold_ = 0;

        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.ljh.gamedemo.proto.protoc.RoleProto.internal_static_Role_descriptor;
      }

      @java.lang.Override
      public com.ljh.gamedemo.proto.protoc.RoleProto.Role getDefaultInstanceForType() {
        return com.ljh.gamedemo.proto.protoc.RoleProto.Role.getDefaultInstance();
      }

      @java.lang.Override
      public com.ljh.gamedemo.proto.protoc.RoleProto.Role build() {
        com.ljh.gamedemo.proto.protoc.RoleProto.Role result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public com.ljh.gamedemo.proto.protoc.RoleProto.Role buildPartial() {
        com.ljh.gamedemo.proto.protoc.RoleProto.Role result = new com.ljh.gamedemo.proto.protoc.RoleProto.Role(this);
        result.roleId_ = roleId_;
        result.name_ = name_;
        result.type_ = type_;
        result.level_ = level_;
        result.alive_ = alive_;
        result.hp_ = hp_;
        result.mp_ = mp_;
        result.gold_ = gold_;
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
        if (other instanceof com.ljh.gamedemo.proto.protoc.RoleProto.Role) {
          return mergeFrom((com.ljh.gamedemo.proto.protoc.RoleProto.Role)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.ljh.gamedemo.proto.protoc.RoleProto.Role other) {
        if (other == com.ljh.gamedemo.proto.protoc.RoleProto.Role.getDefaultInstance()) return this;
        if (other.getRoleId() != 0L) {
          setRoleId(other.getRoleId());
        }
        if (!other.getName().isEmpty()) {
          name_ = other.name_;
          onChanged();
        }
        if (other.getType() != 0) {
          setType(other.getType());
        }
        if (other.getLevel() != 0) {
          setLevel(other.getLevel());
        }
        if (other.getAlive() != 0) {
          setAlive(other.getAlive());
        }
        if (other.getHp() != 0) {
          setHp(other.getHp());
        }
        if (other.getMp() != 0) {
          setMp(other.getMp());
        }
        if (other.getGold() != 0) {
          setGold(other.getGold());
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
        com.ljh.gamedemo.proto.protoc.RoleProto.Role parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.ljh.gamedemo.proto.protoc.RoleProto.Role) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private long roleId_ ;
      /**
       * <code>int64 roleId = 1;</code>
       */
      public long getRoleId() {
        return roleId_;
      }
      /**
       * <code>int64 roleId = 1;</code>
       */
      public Builder setRoleId(long value) {
        
        roleId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int64 roleId = 1;</code>
       */
      public Builder clearRoleId() {
        
        roleId_ = 0L;
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

      private int type_ ;
      /**
       * <code>int32 type = 3;</code>
       */
      public int getType() {
        return type_;
      }
      /**
       * <code>int32 type = 3;</code>
       */
      public Builder setType(int value) {
        
        type_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 type = 3;</code>
       */
      public Builder clearType() {
        
        type_ = 0;
        onChanged();
        return this;
      }

      private int level_ ;
      /**
       * <code>int32 level = 4;</code>
       */
      public int getLevel() {
        return level_;
      }
      /**
       * <code>int32 level = 4;</code>
       */
      public Builder setLevel(int value) {
        
        level_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 level = 4;</code>
       */
      public Builder clearLevel() {
        
        level_ = 0;
        onChanged();
        return this;
      }

      private int alive_ ;
      /**
       * <code>int32 alive = 5;</code>
       */
      public int getAlive() {
        return alive_;
      }
      /**
       * <code>int32 alive = 5;</code>
       */
      public Builder setAlive(int value) {
        
        alive_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 alive = 5;</code>
       */
      public Builder clearAlive() {
        
        alive_ = 0;
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

      private int mp_ ;
      /**
       * <code>int32 mp = 7;</code>
       */
      public int getMp() {
        return mp_;
      }
      /**
       * <code>int32 mp = 7;</code>
       */
      public Builder setMp(int value) {
        
        mp_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 mp = 7;</code>
       */
      public Builder clearMp() {
        
        mp_ = 0;
        onChanged();
        return this;
      }

      private int gold_ ;
      /**
       * <code>int32 gold = 8;</code>
       */
      public int getGold() {
        return gold_;
      }
      /**
       * <code>int32 gold = 8;</code>
       */
      public Builder setGold(int value) {
        
        gold_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 gold = 8;</code>
       */
      public Builder clearGold() {
        
        gold_ = 0;
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


      // @@protoc_insertion_point(builder_scope:Role)
    }

    // @@protoc_insertion_point(class_scope:Role)
    private static final com.ljh.gamedemo.proto.protoc.RoleProto.Role DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new com.ljh.gamedemo.proto.protoc.RoleProto.Role();
    }

    public static com.ljh.gamedemo.proto.protoc.RoleProto.Role getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<Role>
        PARSER = new com.google.protobuf.AbstractParser<Role>() {
      @java.lang.Override
      public Role parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new Role(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<Role> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<Role> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public com.ljh.gamedemo.proto.protoc.RoleProto.Role getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_Role_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_Role_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n/src/main/java/com/ljh/gamedemo/proto/R" +
      "ole.proto\"v\n\004Role\022\016\n\006roleId\030\001 \001(\003\022\014\n\004nam" +
      "e\030\002 \001(\t\022\014\n\004type\030\003 \001(\005\022\r\n\005level\030\004 \001(\005\022\r\n\005" +
      "alive\030\005 \001(\005\022\n\n\002hp\030\006 \001(\005\022\n\n\002mp\030\007 \001(\005\022\014\n\004g" +
      "old\030\010 \001(\005B*\n\035com.ljh.gamedemo.proto.prot" +
      "ocB\tRoleProtob\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_Role_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_Role_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_Role_descriptor,
        new java.lang.String[] { "RoleId", "Name", "Type", "Level", "Alive", "Hp", "Mp", "Gold", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
