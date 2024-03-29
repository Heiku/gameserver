// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: Goods.proto

package com.ljh.gamedemo.proto.protoc;

public final class GoodsProto {
  private GoodsProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface GoodsOrBuilder extends
      // @@protoc_insertion_point(interface_extends:Goods)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>int32 num = 1;</code>
     */
    int getNum();

    /**
     * <code>.Equip equip = 2;</code>
     */
    boolean hasEquip();
    /**
     * <code>.Equip equip = 2;</code>
     */
    com.ljh.gamedemo.proto.protoc.EquipProto.Equip getEquip();
    /**
     * <code>.Equip equip = 2;</code>
     */
    com.ljh.gamedemo.proto.protoc.EquipProto.EquipOrBuilder getEquipOrBuilder();

    /**
     * <code>.Items item = 3;</code>
     */
    boolean hasItem();
    /**
     * <code>.Items item = 3;</code>
     */
    com.ljh.gamedemo.proto.protoc.ItemsProto.Items getItem();
    /**
     * <code>.Items item = 3;</code>
     */
    com.ljh.gamedemo.proto.protoc.ItemsProto.ItemsOrBuilder getItemOrBuilder();
  }
  /**
   * <pre>
   * 物品适配类，用于展示物品信息
   * </pre>
   *
   * Protobuf type {@code Goods}
   */
  public  static final class Goods extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:Goods)
      GoodsOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use Goods.newBuilder() to construct.
    private Goods(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private Goods() {
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new Goods();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private Goods(
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

              num_ = input.readInt32();
              break;
            }
            case 18: {
              com.ljh.gamedemo.proto.protoc.EquipProto.Equip.Builder subBuilder = null;
              if (equip_ != null) {
                subBuilder = equip_.toBuilder();
              }
              equip_ = input.readMessage(com.ljh.gamedemo.proto.protoc.EquipProto.Equip.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(equip_);
                equip_ = subBuilder.buildPartial();
              }

              break;
            }
            case 26: {
              com.ljh.gamedemo.proto.protoc.ItemsProto.Items.Builder subBuilder = null;
              if (item_ != null) {
                subBuilder = item_.toBuilder();
              }
              item_ = input.readMessage(com.ljh.gamedemo.proto.protoc.ItemsProto.Items.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(item_);
                item_ = subBuilder.buildPartial();
              }

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
      return com.ljh.gamedemo.proto.protoc.GoodsProto.internal_static_Goods_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.ljh.gamedemo.proto.protoc.GoodsProto.internal_static_Goods_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.ljh.gamedemo.proto.protoc.GoodsProto.Goods.class, com.ljh.gamedemo.proto.protoc.GoodsProto.Goods.Builder.class);
    }

    public static final int NUM_FIELD_NUMBER = 1;
    private int num_;
    /**
     * <code>int32 num = 1;</code>
     */
    public int getNum() {
      return num_;
    }

    public static final int EQUIP_FIELD_NUMBER = 2;
    private com.ljh.gamedemo.proto.protoc.EquipProto.Equip equip_;
    /**
     * <code>.Equip equip = 2;</code>
     */
    public boolean hasEquip() {
      return equip_ != null;
    }
    /**
     * <code>.Equip equip = 2;</code>
     */
    public com.ljh.gamedemo.proto.protoc.EquipProto.Equip getEquip() {
      return equip_ == null ? com.ljh.gamedemo.proto.protoc.EquipProto.Equip.getDefaultInstance() : equip_;
    }
    /**
     * <code>.Equip equip = 2;</code>
     */
    public com.ljh.gamedemo.proto.protoc.EquipProto.EquipOrBuilder getEquipOrBuilder() {
      return getEquip();
    }

    public static final int ITEM_FIELD_NUMBER = 3;
    private com.ljh.gamedemo.proto.protoc.ItemsProto.Items item_;
    /**
     * <code>.Items item = 3;</code>
     */
    public boolean hasItem() {
      return item_ != null;
    }
    /**
     * <code>.Items item = 3;</code>
     */
    public com.ljh.gamedemo.proto.protoc.ItemsProto.Items getItem() {
      return item_ == null ? com.ljh.gamedemo.proto.protoc.ItemsProto.Items.getDefaultInstance() : item_;
    }
    /**
     * <code>.Items item = 3;</code>
     */
    public com.ljh.gamedemo.proto.protoc.ItemsProto.ItemsOrBuilder getItemOrBuilder() {
      return getItem();
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
      if (num_ != 0) {
        output.writeInt32(1, num_);
      }
      if (equip_ != null) {
        output.writeMessage(2, getEquip());
      }
      if (item_ != null) {
        output.writeMessage(3, getItem());
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (num_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, num_);
      }
      if (equip_ != null) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(2, getEquip());
      }
      if (item_ != null) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(3, getItem());
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
      if (!(obj instanceof com.ljh.gamedemo.proto.protoc.GoodsProto.Goods)) {
        return super.equals(obj);
      }
      com.ljh.gamedemo.proto.protoc.GoodsProto.Goods other = (com.ljh.gamedemo.proto.protoc.GoodsProto.Goods) obj;

      if (getNum()
          != other.getNum()) return false;
      if (hasEquip() != other.hasEquip()) return false;
      if (hasEquip()) {
        if (!getEquip()
            .equals(other.getEquip())) return false;
      }
      if (hasItem() != other.hasItem()) return false;
      if (hasItem()) {
        if (!getItem()
            .equals(other.getItem())) return false;
      }
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
      hash = (37 * hash) + NUM_FIELD_NUMBER;
      hash = (53 * hash) + getNum();
      if (hasEquip()) {
        hash = (37 * hash) + EQUIP_FIELD_NUMBER;
        hash = (53 * hash) + getEquip().hashCode();
      }
      if (hasItem()) {
        hash = (37 * hash) + ITEM_FIELD_NUMBER;
        hash = (53 * hash) + getItem().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static com.ljh.gamedemo.proto.protoc.GoodsProto.Goods parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.ljh.gamedemo.proto.protoc.GoodsProto.Goods parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.ljh.gamedemo.proto.protoc.GoodsProto.Goods parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.ljh.gamedemo.proto.protoc.GoodsProto.Goods parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.ljh.gamedemo.proto.protoc.GoodsProto.Goods parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.ljh.gamedemo.proto.protoc.GoodsProto.Goods parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.ljh.gamedemo.proto.protoc.GoodsProto.Goods parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.ljh.gamedemo.proto.protoc.GoodsProto.Goods parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.ljh.gamedemo.proto.protoc.GoodsProto.Goods parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static com.ljh.gamedemo.proto.protoc.GoodsProto.Goods parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.ljh.gamedemo.proto.protoc.GoodsProto.Goods parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.ljh.gamedemo.proto.protoc.GoodsProto.Goods parseFrom(
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
    public static Builder newBuilder(com.ljh.gamedemo.proto.protoc.GoodsProto.Goods prototype) {
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
     * <pre>
     * 物品适配类，用于展示物品信息
     * </pre>
     *
     * Protobuf type {@code Goods}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:Goods)
        com.ljh.gamedemo.proto.protoc.GoodsProto.GoodsOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.ljh.gamedemo.proto.protoc.GoodsProto.internal_static_Goods_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.ljh.gamedemo.proto.protoc.GoodsProto.internal_static_Goods_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.ljh.gamedemo.proto.protoc.GoodsProto.Goods.class, com.ljh.gamedemo.proto.protoc.GoodsProto.Goods.Builder.class);
      }

      // Construct using com.ljh.gamedemo.proto.protoc.GoodsProto.Goods.newBuilder()
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
        num_ = 0;

        if (equipBuilder_ == null) {
          equip_ = null;
        } else {
          equip_ = null;
          equipBuilder_ = null;
        }
        if (itemBuilder_ == null) {
          item_ = null;
        } else {
          item_ = null;
          itemBuilder_ = null;
        }
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.ljh.gamedemo.proto.protoc.GoodsProto.internal_static_Goods_descriptor;
      }

      @java.lang.Override
      public com.ljh.gamedemo.proto.protoc.GoodsProto.Goods getDefaultInstanceForType() {
        return com.ljh.gamedemo.proto.protoc.GoodsProto.Goods.getDefaultInstance();
      }

      @java.lang.Override
      public com.ljh.gamedemo.proto.protoc.GoodsProto.Goods build() {
        com.ljh.gamedemo.proto.protoc.GoodsProto.Goods result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public com.ljh.gamedemo.proto.protoc.GoodsProto.Goods buildPartial() {
        com.ljh.gamedemo.proto.protoc.GoodsProto.Goods result = new com.ljh.gamedemo.proto.protoc.GoodsProto.Goods(this);
        result.num_ = num_;
        if (equipBuilder_ == null) {
          result.equip_ = equip_;
        } else {
          result.equip_ = equipBuilder_.build();
        }
        if (itemBuilder_ == null) {
          result.item_ = item_;
        } else {
          result.item_ = itemBuilder_.build();
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
        if (other instanceof com.ljh.gamedemo.proto.protoc.GoodsProto.Goods) {
          return mergeFrom((com.ljh.gamedemo.proto.protoc.GoodsProto.Goods)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.ljh.gamedemo.proto.protoc.GoodsProto.Goods other) {
        if (other == com.ljh.gamedemo.proto.protoc.GoodsProto.Goods.getDefaultInstance()) return this;
        if (other.getNum() != 0) {
          setNum(other.getNum());
        }
        if (other.hasEquip()) {
          mergeEquip(other.getEquip());
        }
        if (other.hasItem()) {
          mergeItem(other.getItem());
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
        com.ljh.gamedemo.proto.protoc.GoodsProto.Goods parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.ljh.gamedemo.proto.protoc.GoodsProto.Goods) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private int num_ ;
      /**
       * <code>int32 num = 1;</code>
       */
      public int getNum() {
        return num_;
      }
      /**
       * <code>int32 num = 1;</code>
       */
      public Builder setNum(int value) {
        
        num_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 num = 1;</code>
       */
      public Builder clearNum() {
        
        num_ = 0;
        onChanged();
        return this;
      }

      private com.ljh.gamedemo.proto.protoc.EquipProto.Equip equip_;
      private com.google.protobuf.SingleFieldBuilderV3<
          com.ljh.gamedemo.proto.protoc.EquipProto.Equip, com.ljh.gamedemo.proto.protoc.EquipProto.Equip.Builder, com.ljh.gamedemo.proto.protoc.EquipProto.EquipOrBuilder> equipBuilder_;
      /**
       * <code>.Equip equip = 2;</code>
       */
      public boolean hasEquip() {
        return equipBuilder_ != null || equip_ != null;
      }
      /**
       * <code>.Equip equip = 2;</code>
       */
      public com.ljh.gamedemo.proto.protoc.EquipProto.Equip getEquip() {
        if (equipBuilder_ == null) {
          return equip_ == null ? com.ljh.gamedemo.proto.protoc.EquipProto.Equip.getDefaultInstance() : equip_;
        } else {
          return equipBuilder_.getMessage();
        }
      }
      /**
       * <code>.Equip equip = 2;</code>
       */
      public Builder setEquip(com.ljh.gamedemo.proto.protoc.EquipProto.Equip value) {
        if (equipBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          equip_ = value;
          onChanged();
        } else {
          equipBuilder_.setMessage(value);
        }

        return this;
      }
      /**
       * <code>.Equip equip = 2;</code>
       */
      public Builder setEquip(
          com.ljh.gamedemo.proto.protoc.EquipProto.Equip.Builder builderForValue) {
        if (equipBuilder_ == null) {
          equip_ = builderForValue.build();
          onChanged();
        } else {
          equipBuilder_.setMessage(builderForValue.build());
        }

        return this;
      }
      /**
       * <code>.Equip equip = 2;</code>
       */
      public Builder mergeEquip(com.ljh.gamedemo.proto.protoc.EquipProto.Equip value) {
        if (equipBuilder_ == null) {
          if (equip_ != null) {
            equip_ =
              com.ljh.gamedemo.proto.protoc.EquipProto.Equip.newBuilder(equip_).mergeFrom(value).buildPartial();
          } else {
            equip_ = value;
          }
          onChanged();
        } else {
          equipBuilder_.mergeFrom(value);
        }

        return this;
      }
      /**
       * <code>.Equip equip = 2;</code>
       */
      public Builder clearEquip() {
        if (equipBuilder_ == null) {
          equip_ = null;
          onChanged();
        } else {
          equip_ = null;
          equipBuilder_ = null;
        }

        return this;
      }
      /**
       * <code>.Equip equip = 2;</code>
       */
      public com.ljh.gamedemo.proto.protoc.EquipProto.Equip.Builder getEquipBuilder() {
        
        onChanged();
        return getEquipFieldBuilder().getBuilder();
      }
      /**
       * <code>.Equip equip = 2;</code>
       */
      public com.ljh.gamedemo.proto.protoc.EquipProto.EquipOrBuilder getEquipOrBuilder() {
        if (equipBuilder_ != null) {
          return equipBuilder_.getMessageOrBuilder();
        } else {
          return equip_ == null ?
              com.ljh.gamedemo.proto.protoc.EquipProto.Equip.getDefaultInstance() : equip_;
        }
      }
      /**
       * <code>.Equip equip = 2;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          com.ljh.gamedemo.proto.protoc.EquipProto.Equip, com.ljh.gamedemo.proto.protoc.EquipProto.Equip.Builder, com.ljh.gamedemo.proto.protoc.EquipProto.EquipOrBuilder> 
          getEquipFieldBuilder() {
        if (equipBuilder_ == null) {
          equipBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              com.ljh.gamedemo.proto.protoc.EquipProto.Equip, com.ljh.gamedemo.proto.protoc.EquipProto.Equip.Builder, com.ljh.gamedemo.proto.protoc.EquipProto.EquipOrBuilder>(
                  getEquip(),
                  getParentForChildren(),
                  isClean());
          equip_ = null;
        }
        return equipBuilder_;
      }

      private com.ljh.gamedemo.proto.protoc.ItemsProto.Items item_;
      private com.google.protobuf.SingleFieldBuilderV3<
          com.ljh.gamedemo.proto.protoc.ItemsProto.Items, com.ljh.gamedemo.proto.protoc.ItemsProto.Items.Builder, com.ljh.gamedemo.proto.protoc.ItemsProto.ItemsOrBuilder> itemBuilder_;
      /**
       * <code>.Items item = 3;</code>
       */
      public boolean hasItem() {
        return itemBuilder_ != null || item_ != null;
      }
      /**
       * <code>.Items item = 3;</code>
       */
      public com.ljh.gamedemo.proto.protoc.ItemsProto.Items getItem() {
        if (itemBuilder_ == null) {
          return item_ == null ? com.ljh.gamedemo.proto.protoc.ItemsProto.Items.getDefaultInstance() : item_;
        } else {
          return itemBuilder_.getMessage();
        }
      }
      /**
       * <code>.Items item = 3;</code>
       */
      public Builder setItem(com.ljh.gamedemo.proto.protoc.ItemsProto.Items value) {
        if (itemBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          item_ = value;
          onChanged();
        } else {
          itemBuilder_.setMessage(value);
        }

        return this;
      }
      /**
       * <code>.Items item = 3;</code>
       */
      public Builder setItem(
          com.ljh.gamedemo.proto.protoc.ItemsProto.Items.Builder builderForValue) {
        if (itemBuilder_ == null) {
          item_ = builderForValue.build();
          onChanged();
        } else {
          itemBuilder_.setMessage(builderForValue.build());
        }

        return this;
      }
      /**
       * <code>.Items item = 3;</code>
       */
      public Builder mergeItem(com.ljh.gamedemo.proto.protoc.ItemsProto.Items value) {
        if (itemBuilder_ == null) {
          if (item_ != null) {
            item_ =
              com.ljh.gamedemo.proto.protoc.ItemsProto.Items.newBuilder(item_).mergeFrom(value).buildPartial();
          } else {
            item_ = value;
          }
          onChanged();
        } else {
          itemBuilder_.mergeFrom(value);
        }

        return this;
      }
      /**
       * <code>.Items item = 3;</code>
       */
      public Builder clearItem() {
        if (itemBuilder_ == null) {
          item_ = null;
          onChanged();
        } else {
          item_ = null;
          itemBuilder_ = null;
        }

        return this;
      }
      /**
       * <code>.Items item = 3;</code>
       */
      public com.ljh.gamedemo.proto.protoc.ItemsProto.Items.Builder getItemBuilder() {
        
        onChanged();
        return getItemFieldBuilder().getBuilder();
      }
      /**
       * <code>.Items item = 3;</code>
       */
      public com.ljh.gamedemo.proto.protoc.ItemsProto.ItemsOrBuilder getItemOrBuilder() {
        if (itemBuilder_ != null) {
          return itemBuilder_.getMessageOrBuilder();
        } else {
          return item_ == null ?
              com.ljh.gamedemo.proto.protoc.ItemsProto.Items.getDefaultInstance() : item_;
        }
      }
      /**
       * <code>.Items item = 3;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          com.ljh.gamedemo.proto.protoc.ItemsProto.Items, com.ljh.gamedemo.proto.protoc.ItemsProto.Items.Builder, com.ljh.gamedemo.proto.protoc.ItemsProto.ItemsOrBuilder> 
          getItemFieldBuilder() {
        if (itemBuilder_ == null) {
          itemBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              com.ljh.gamedemo.proto.protoc.ItemsProto.Items, com.ljh.gamedemo.proto.protoc.ItemsProto.Items.Builder, com.ljh.gamedemo.proto.protoc.ItemsProto.ItemsOrBuilder>(
                  getItem(),
                  getParentForChildren(),
                  isClean());
          item_ = null;
        }
        return itemBuilder_;
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


      // @@protoc_insertion_point(builder_scope:Goods)
    }

    // @@protoc_insertion_point(class_scope:Goods)
    private static final com.ljh.gamedemo.proto.protoc.GoodsProto.Goods DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new com.ljh.gamedemo.proto.protoc.GoodsProto.Goods();
    }

    public static com.ljh.gamedemo.proto.protoc.GoodsProto.Goods getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<Goods>
        PARSER = new com.google.protobuf.AbstractParser<Goods>() {
      @java.lang.Override
      public Goods parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new Goods(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<Goods> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<Goods> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public com.ljh.gamedemo.proto.protoc.GoodsProto.Goods getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_Goods_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_Goods_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\013Goods.proto\032\013Equip.proto\032\013Items.proto\"" +
      "A\n\005Goods\022\013\n\003num\030\001 \001(\005\022\025\n\005equip\030\002 \001(\0132\006.E" +
      "quip\022\024\n\004item\030\003 \001(\0132\006.ItemsB+\n\035com.ljh.ga" +
      "medemo.proto.protocB\nGoodsProtob\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.ljh.gamedemo.proto.protoc.EquipProto.getDescriptor(),
          com.ljh.gamedemo.proto.protoc.ItemsProto.getDescriptor(),
        });
    internal_static_Goods_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_Goods_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_Goods_descriptor,
        new java.lang.String[] { "Num", "Equip", "Item", });
    com.ljh.gamedemo.proto.protoc.EquipProto.getDescriptor();
    com.ljh.gamedemo.proto.protoc.ItemsProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
