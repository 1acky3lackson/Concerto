package top.gregtao.concerto.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import top.gregtao.concerto.enums.OrderType;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class OrderTypeArgumentType implements ArgumentType<OrderType> {

    private OrderTypeArgumentType() {
    }

    public static OrderTypeArgumentType orderType() {
        return new OrderTypeArgumentType();
    }

    public static OrderType getOrderType(CommandContext<FabricClientCommandSource> context, String id) {
        return context.getArgument(id, OrderType.class);
    }

    @Override
    public OrderType parse(StringReader reader) throws CommandSyntaxException {
        String s = reader.readUnquotedString();
        for (OrderType type : OrderType.values()) {
            if (type.asString().equals(s)) return type;
        }
        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect().create(s);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(Arrays.stream(OrderType.values()).map(OrderType::asString), builder);
    }
}
